package com.afonso.api.hubspot.config;

import com.afonso.api.hubspot.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;


/**
 * Implementação da interface {@link OAuth2AuthorizedClientRepository} que é usada internamente no
 * {@link OAuth2AuthorizedClientManager} para carregar, salva e remover {@link OAuth2AuthorizedClient}. Essa personalização
 *  foi necessária para tratar {@link Authentication} anônimos. A implementação padrão busca esse tipo de informação na session http.
 * */
@Component
@Primary
@RequiredArgsConstructor
public class SimpleOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {
  private final OAuth2AuthorizedClientService authorizedClientService;
  private static final Authentication authentication;

  public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
    return this.authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName()) ;
  }

  public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
      this.authorizedClientService.saveAuthorizedClient(authorizedClient, authentication);
  }

  public void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
      this.authorizedClientService.removeAuthorizedClient(clientRegistrationId, authentication.getName());
  }

  static {
    authentication = Utils.defaultAuthentication();
  }
}
