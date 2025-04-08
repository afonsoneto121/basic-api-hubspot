package com.afonso.api.hubspot.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Esta interface é utilizada para geração automática de documentação via Swagger/OpenAPI
 * e pode ser implementada por um controller para garantir consistência na API exposta.
 * A separacao da classe concreta é necessaria para melhorar a legibilidade
 */
public interface IHubSpotController {

    @Operation(summary = "Geração da Authorization URL",
            description = "Endpoint responsável por gerar e retornar a URL de autorização para iniciar o " +
                    "fluxo OAuth com o HubSpot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro")
    })
    ResponseEntity<String> urlGenerate();

    @Operation(summary = "Processamento do Callback OAuth",
            description = "Endpoint recebe o código de autorização fornecido pelo HubSpot e realiza a " +
                    "troca pelo token de acesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro"),
            @ApiResponse(responseCode = "201", description = "Usuario nao autorizado")
    })
    ResponseEntity<String> oauthCallback(@RequestParam(value = "code", required = false) String authorizationCode);
}