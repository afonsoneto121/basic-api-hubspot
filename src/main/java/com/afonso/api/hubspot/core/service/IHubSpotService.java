package com.afonso.api.hubspot.core.service;

import com.afonso.api.hubspot.api.dto.WebhookDTO;
import com.afonso.api.hubspot.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.List;

/**
 * Interface responsável por gerenciar a autenticação OAuth com o HubSpot e validar a assinatura dos webhooks recebidos.
 *
 */
public interface IHubSpotService {

    /**
     * Gera a URL de autorização que o usuário deve acessar para iniciar o fluxo OAuth com o HubSpot. A geracao usa
     * dados de {@link ClientRegistration} configurados no `application.yml`
     *
     * @return A URL de autorização formatada com clientId, redirectUri e scopes codificados.
     * @throws CustomException caso o client registration do HubSpot não seja encontrado.
     */
    String urlGenerate();

    /**
     * Processa o código de autorização recebido no callback OAuth e realiza a troca pelo token de acesso.
     * O token gerado é salvo no {@link OAuth2AuthorizedClientService} com um @link {@link Authentication} default,
     * que será usado para recuperar o token no {@link OAuth2AuthorizedClientManager} .
     *
     * @param code Código de autorização fornecido pela HubSpot após o consentimento do usuário.
     * @throws CustomException em caso de falha na troca do código por token ou qualquer outra exceção.
     */
    void callbackAuth(String code);

    /**
     * Valida a assinatura na versao v1 de um webhook enviado pela HubSpot.
     * A assinatura é gerada concatenando o clientSecret + corpo da requisição,
     * e aplicando SHA-256 sobre essa string. Essa validação é importante para garantir que somente request
     * com origem no HubSpot seja processada.
     *
     * @param webhookDTO Lista de eventos recebidos do webhook.
     * @param signature Assinatura recebida no header "X-HubSpot-Signature".
     * @return {@code true} se a assinatura for válida, {@code false} caso contrário.
     * @throws CustomException em caso de erro ao serializar o corpo da requisição.
     */
    boolean validateSignatureV1(List<WebhookDTO> webhookDTO, String signature);

}
