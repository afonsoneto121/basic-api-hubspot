package com.afonso.api.hubspot.api.controller;

import com.afonso.api.hubspot.api.IHubSpotController;
import com.afonso.api.hubspot.core.service.IHubSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class HubSpotController implements IHubSpotController {

  private final IHubSpotService hubSpotService;

  @GetMapping("v1/url-generate")
  public ResponseEntity<String> urlGenerate() {
    log.atTrace().log("Iniciando geracao de URL");
    String urlGenerate = hubSpotService.urlGenerate();
    log.atTrace().log("Finalizando geracao de URL {}", urlGenerate);
    return ResponseEntity.of(Optional.of(urlGenerate));
  }

  @GetMapping("v1/oauth-callback")
  public ResponseEntity<String> oauthCallback(@RequestParam(value = "code", required = false) String authorizationCode) {
    log.atTrace().log("Inciando authCallback");
    hubSpotService.callbackAuth(authorizationCode);
    String html = """
      <!DOCTYPE html>
      <html lang="pt-BR">
      <head>
          <meta charset="UTF-8">
          <title>Autenticação concluída</title>
      </head>
      <body>
          <h2>Autenticação concluída com sucesso!</h2>
          <p>Você pode fechar esta guia.</p>
      </body>
      </html>
      """;

    log.atTrace().log("Finalizando authCallback");
    return ResponseEntity.ok()
      .contentType(MediaType.TEXT_HTML)
      .body(html);
  }

}
