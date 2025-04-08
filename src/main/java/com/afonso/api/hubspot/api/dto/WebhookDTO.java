package com.afonso.api.hubspot.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WebhookDTO {
  private Long eventId;
  private Long subscriptionId;
  private Long portalId;
  private Long appId;
  private Long occurredAt;
  private String subscriptionType;
  private Integer attemptNumber;
  private Long objectId;
  private String changeFlag;
  private String changeSource;
  private String sourceId;
}
