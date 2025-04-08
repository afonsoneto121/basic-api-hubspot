package com.afonso.api.hubspot.core.service;

import com.afonso.api.hubspot.api.dto.ContactDTO;
import com.afonso.api.hubspot.api.dto.WebhookDTO;
import com.afonso.api.hubspot.exception.CustomException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Interface responsável por interagir com a API do HubSpot
 * para criar contatos e processar webhooks recebidos.
 *
 * <p>
 *  Utiliza {@link WebClient} para comunicação com a API do HubSpot e inclui tratamento robusto
 * de erros, como exceções de autenticação e limites de requisição (rate limiting).
 */
public interface IContactService {

  /**
   * Realiza a criação de um novo contato na API do HubSpot.
   * <p>
   * Envia uma requisição HTTP POST com os dados do contato. A resposta não possui corpo, e
   * em caso de sucesso o mesmo {@link ContactDTO} recebido é retornado.
   * <p>
   * O {@link WebClient} injetado foi configurado com o {@link OAuth2AuthorizedClientManager} para evitar gerenciamento
   * manual do token
   *
   * @param contactDTO Objeto contendo os dados do contato a ser criado.
   * @return O mesmo objeto {@link ContactDTO} enviado, em caso de sucesso.
   * @throws CustomException em caso de falhas durante a comunicação com a API.
   */
  ContactDTO createContact(ContactDTO contactDTO);

  /**
   * Manipula eventos recebidos via webhook da HubSpot.
   *
   * <p>Valida a assinatura do webhook usando {@linkplain  IHubSpotService#validateSignatureV1}
   * para garantir a autenticidade dos dados. Se a assinatura for válida, processa os dados recebidos.</p>
   *
   * @param webhook   Lista de eventos enviados pela HubSpot.
   * @param signature Assinatura enviada no header da requisição.
   */
  void handlerWebhook(List<WebhookDTO> webhook, String signature);
}
