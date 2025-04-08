package com.afonso.api.hubspot.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Contact {
  private String email;
  private String firstname;
  private String lastname;
}
