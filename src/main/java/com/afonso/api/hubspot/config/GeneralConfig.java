package com.afonso.api.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Classe de configuração responsável por definir os beans relacionados à autenticação OAuth2
 * e ao cliente WebClient para comunicação com a API do HubSpot.
 *
 * <p>Inclui a configuração do {@link WebClient} com suporte automático à autenticação OAuth2,
 * e personalização do fluxo de autorização e atualização de tokens.</p>
 */
@Configuration
public class GeneralConfig {

  private static final String BASE_URL = "https://api.hubapi.com/";
  private static final String HUBSPOT = "hubspot";

  /**
   * Classe de configuração responsável por definir os beans relacionados à autenticação OAuth2
   * e ao cliente WebClient para comunicação com a API do HubSpot.
   * <p>
   * Essa configuração é necessária para que o OAuth2 Client gerencie os token de forma automática, ou seja não é
   * necessário refresh manual do token.
   */
  @Bean
  public WebClient webClient(OAuth2AuthorizedClientManager manager) {
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
      new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
    oauth2.setDefaultClientRegistrationId(HUBSPOT);
    return WebClient.builder()
      .baseUrl(BASE_URL)
      .apply(oauth2.oauth2Configuration())
      .build();
  }

  /**
   * Configura o {@link OAuth2AuthorizedClientManager}, responsável por fornecer e renovar tokens
   * OAuth2 para chamadas autenticadas. Refresh token foi customizado para usar o {@link RestClientRefreshTokenTokenResponseClient}
   * por 2 motivos:
   * <p>
   *   1. Manter compatibilidade com versoes futuras do Spring Security, pois o {@link DefaultRefreshTokenTokenResponseClient} será
   *   descontinuado e trocado pelo {@link RestClientRefreshTokenTokenResponseClient}
   *   <p>
   *   2. Os parametros default do cliente não são compatíveis com o HubSpot, falta client_id e secret_id na request
   */
  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
    ClientRegistrationRepository clientRegistrationRepository,
    OAuth2AuthorizedClientRepository authorizedClientRepository) {

    OAuth2AuthorizedClientProvider authorizedClientProvider =
      OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken( refreshTokenGrantBuilder ->
          refreshTokenGrantBuilder.accessTokenResponseClient(getRefreshConfig())
        )
        .clientCredentials()
        .build();

    DefaultOAuth2AuthorizedClientManager authorizedClientManager =
      new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  /**
   * Cria um {@link RestClientAuthorizationCodeTokenResponseClient} personalizado
   * para lidar com a troca de código de autorização por token de acesso. Foi customizado por 2 motivos:
   * <p>
   *   1. Manter compatibilidade com versoes futuras do Spring Security, pois o {@link DefaultAuthorizationCodeTokenResponseClient} será
   *   descontinuado e trocado pelo {@link RestClientAuthorizationCodeTokenResponseClient}
   *   <p>
   *   2. Os parametros default do cliente não são compatíveis com o HubSpot, falta client_id e secret_id na request   .
   */

  @Bean
  public RestClientAuthorizationCodeTokenResponseClient restClientAuthorizationCodeTokenResponseClient() {
    RestClientAuthorizationCodeTokenResponseClient client = new RestClientAuthorizationCodeTokenResponseClient();
    client.addParametersConverter(grantRequest -> {
      ClientRegistration clientRegistration = grantRequest.getClientRegistration();
      MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
      if (clientRegistration.getRegistrationId().equals(HUBSPOT)) {
        parameters.set(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        parameters.set(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
      }
      return parameters;
    });
    return client;
  }

  public RestClientRefreshTokenTokenResponseClient getRefreshConfig() {
    RestClientRefreshTokenTokenResponseClient client = new RestClientRefreshTokenTokenResponseClient();
    client.addParametersConverter(grantRequest -> {
      ClientRegistration clientRegistration = grantRequest.getClientRegistration();
      MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
      if (clientRegistration.getRegistrationId().equals(HUBSPOT)) {
        parameters.set(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        parameters.set(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
      }
      return parameters;
    });
    return client;
  }
}
