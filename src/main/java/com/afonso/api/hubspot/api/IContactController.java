package com.afonso.api.hubspot.api;

import com.afonso.api.hubspot.api.dto.ContactDTO;
import com.afonso.api.hubspot.api.dto.WebhookDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Esta interface é utilizada para geração automática de documentação via Swagger/OpenAPI
 * e pode ser implementada por um controller para garantir consistência na API exposta.
 * A separacao da classe concreta é necessaria para melhorar a legibilidade
 */
public interface IContactController {

    @Operation(summary = "Processamento do Callback OAuth",
        description = "Endpoint recebe o código de autorização fornecido pelo HubSpot e realiza a " +
            "troca pelo token de acesso.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contato Criado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro"),
        @ApiResponse(responseCode = "401", description = "Usuario nao autorizado"),
        @ApiResponse(responseCode = "409", description = "Usuario duplicado ")
    })
    ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO contactDTO);

    @Operation(summary = "Recebimento de Webhook para Criação de Contatos",
        description = "Endpoint que escuta e processa eventos do tipo \"contact.creation\", enviados" +
          "pelo webhook do HubSpot.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook recebido com sucesso"),
    })
    ResponseEntity<Void> webhook(@RequestBody List<WebhookDTO> webhook,
                                 @RequestHeader(value = "X-HubSpot-Signature", required = false) String signature);
}