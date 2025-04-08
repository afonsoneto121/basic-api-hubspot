package com.afonso.api.hubspot.core.service.imp;

import com.afonso.api.hubspot.api.dto.ContactDTO;
import com.afonso.api.hubspot.api.dto.WebhookDTO;
import com.afonso.api.hubspot.core.service.IContactService;
import com.afonso.api.hubspot.core.service.IHubSpotService;
import com.afonso.api.hubspot.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService implements IContactService {

  private static final String ERRO_AO_PROCESSAR_REQUISICAO_HTTP = "Erro ao processar requisicao HTTP";
  private final WebClient webClient;
  private final IHubSpotService hubSpotService;

  @Override
  public ContactDTO createContact(ContactDTO contactDTO) {
    String uri = "crm/v3/objects/contacts" ;
    try {
    webClient.post()
      .uri(uri)
      .headers(it ->
        it.setContentType(MediaType.APPLICATION_JSON)
      )
      .bodyValue(Map.of("properties", contactDTO.toDomain()))
      .retrieve()
      .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, response ->
        Mono.error(new CustomException("Rate limit excedido", HttpStatus.TOO_MANY_REQUESTS)))
      .toBodilessEntity()
      .retryWhen(
        Retry.backoff(3, Duration.ofSeconds(10))
          .filter(CustomException.class::isInstance)
          .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
            retrySignal.failure()
          )
      )
      .block();
    } catch (OAuth2AuthorizationException e) {
      log.atTrace().log(ERRO_AO_PROCESSAR_REQUISICAO_HTTP);
      log.error("invalid_token_response {}", e.getMessage(), e);
      throw new CustomException(ERRO_AO_PROCESSAR_REQUISICAO_HTTP, HttpStatus.UNAUTHORIZED);
    } catch (WebClientResponseException e) {
      log.atTrace().log(ERRO_AO_PROCESSAR_REQUISICAO_HTTP);
      log.error("Client erro {}", e.getMessage(), e);
      throw new CustomException("Client erro", HttpStatus.resolve(e.getStatusCode().value()));
    }
    catch (Exception e) {
      log.atTrace().log("callback finalizaddo com erro");
      log.error(ERRO_AO_PROCESSAR_REQUISICAO_HTTP, e);
      throw new CustomException(ERRO_AO_PROCESSAR_REQUISICAO_HTTP, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return contactDTO;
  }

  @Override
  public void handlerWebhook(List<WebhookDTO> webhook, String signature) {
    if (hubSpotService.validateSignatureV1(webhook, signature)) {
      log.info("Informacao recebido via Webhook {}", webhook);
      todoSomething();
    }
  }

  public void todoSomething() {
    log.info("TODO");
  }
}
