package com.afonso.api.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v1/**",
              "/contact/**",
              "/swagger-ui/**",
              "/v3/api-docs/**"
            ).permitAll()
            .anyRequest()
          .authenticated()
        )
      .csrf(AbstractHttpConfigurer::disable)
      .oauth2Client(oauth2 -> oauth2
        .authorizationCodeGrant(Customizer.withDefaults())
      );

    return http.build();
  }


}