package com.afonso.api.hubspot.api.controller;

import com.afonso.api.hubspot.api.IContactController;
import com.afonso.api.hubspot.api.dto.ContactDTO;
import com.afonso.api.hubspot.api.dto.WebhookDTO;
import com.afonso.api.hubspot.core.service.IContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/contact/")
@RequiredArgsConstructor
@Slf4j
public class ContactController implements IContactController {

  private final IContactService contactService;

  @PostMapping("v1/create-contact")
  public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO contactDTO) {
    log.atTrace().log("Iniciando criacao de contato {}", contactDTO);
    ContactDTO contact = contactService.createContact(contactDTO);
    log.atTrace().log("Finalizando criacao de contato {}", contactDTO);
    return ResponseEntity.created(URI.create(contact.email())).body(contact);
  }

  @PostMapping("v1/webhook")
  public ResponseEntity<Void> webhook(@RequestBody List<WebhookDTO> webhook,
                                      @RequestHeader(value = "X-HubSpot-Signature", required = false) String signature) {
    log.atTrace().log("Iniciando webhook de contato.create {}", webhook);
    contactService.handlerWebhook(webhook, signature);
    log.atTrace().log("Finalizando webhook de contato.create {}", webhook);
    return ResponseEntity.ok().build();
  }
}
