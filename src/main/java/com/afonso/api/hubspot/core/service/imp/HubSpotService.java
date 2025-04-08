package com.afonso.api.hubspot.core.service.imp;

import com.afonso.api.hubspot.api.dto.WebhookDTO;
import com.afonso.api.hubspot.core.service.IHubSpotService;
import com.afonso.api.hubspot.exception.CustomException;
import com.afonso.api.hubspot.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.afonso.api.hubspot.utils.Utils.encode;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubSpotService implements IHubSpotService {
  private static final String HUBSPOT = "hubspot";

  private final ClientRegistrationRepository clientRegistrationRepository;
  private final RestClientAuthorizationCodeTokenResponseClient tokenResponseClient;
  private final OAuth2AuthorizedClientService authorizedClientService;

  @Override
  public String urlGenerate() {
    ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(HUBSPOT);
    if (clientRegistration == null) {
      throw new CustomException("ClientRegistration nao encontrado, por favor considere registar um client",
        HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return clientRegistration.getProviderDetails().getAuthorizationUri()
      + "?client_id=" + encode(clientRegistration.getClientId())
      + "&redirect_uri=" + encode(clientRegistration.getRedirectUri())
      + "&scope=" + encode(String.join(" ", clientRegistration.getScopes()));
  }

  @Override
  public void callbackAuth(String code) {
    try {
      log.atTrace().log("Iniciando callback com code = {}", code);
      ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(HUBSPOT);
      OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
        .authorizationUri(registration.getProviderDetails().getAuthorizationUri())
        .clientId(registration.getClientId())
        .redirectUri(registration.getRedirectUri())
        .build();
      OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(code)
        .redirectUri(registration.getRedirectUri())
        .build();
      OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
      OAuth2AuthorizationCodeGrantRequest tokenRequest = new OAuth2AuthorizationCodeGrantRequest(registration, exchange);
      OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(tokenRequest);

      OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(registration, HUBSPOT, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
      Authentication user = Utils.defaultAuthentication();
      authorizedClientService.saveAuthorizedClient(authorizedClient, user);

      log.atTrace().log("callback finalizaddo com sucesso");
    } catch (OAuth2AuthorizationException e) {
      log.atTrace().log("callback finalizaddo com erro");
      log.error("invalid_token_response {}", e.getMessage(), e);
      throw new CustomException("Erro ao processar callback", e, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      log.atTrace().log("callback finalizaddo com erro");
      log.error("Erro ao processar callback", e);
      throw new CustomException("Erro ao processar callback", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public boolean validateSignatureV1(List<WebhookDTO> webhookDTO, String signature){
    ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(HUBSPOT);
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = null;
    try {
      payload = objectMapper.writeValueAsString(webhookDTO);
    } catch (JsonProcessingException e) {
      throw new CustomException(e.getMessage(),e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    String hash = Hashing.sha256()
      .hashString(registration.getClientSecret() + payload, StandardCharsets.UTF_8)
      .toString();
    return hash.equals(signature);
  }
}
