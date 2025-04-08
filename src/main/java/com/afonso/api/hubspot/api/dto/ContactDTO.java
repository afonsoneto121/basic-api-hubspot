package com.afonso.api.hubspot.api.dto;

import com.afonso.api.hubspot.domain.Contact;

public record ContactDTO(String email,
                         String firstname,
                         String lastname) {
  public Contact toDomain() {
    return Contact.builder()
      .email(email)
      .firstname(firstname)
      .lastname(lastname)
      .build();
  }
}
