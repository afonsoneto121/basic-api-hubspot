package com.afonso.api.hubspot.core.service.imp;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class HubSpotServiceTest {

  @Autowired
  private HubSpotService hubSpotService;

  @Autowired
  private RestClientAuthorizationCodeTokenResponseClient tokenResponseClient;

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  @Test
  void callbackSaveAuthorizedClient() {
    OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken("access-token")
      .tokenType(OAuth2AccessToken.TokenType.BEARER)
      .expiresIn(3600)
      .refreshToken("refresh-token")
      .build();

    when(tokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(tokenResponse);

    hubSpotService.callbackAuth("auth-code");

    verify(authorizedClientService).saveAuthorizedClient(any(), any());
  }

  @TestConfiguration
  static class MockConfig {
    @Bean
    public RestClientAuthorizationCodeTokenResponseClient tokenResponseClient() {
      return mock(RestClientAuthorizationCodeTokenResponseClient.class);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
      return mock(OAuth2AuthorizedClientService.class);
    }
  }
}