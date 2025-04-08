package com.afonso.api.hubspot.core.service.imp;

import com.afonso.api.hubspot.api.dto.ContactDTO;
import com.afonso.api.hubspot.core.service.IHubSpotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContactServiceTest {

  public static MockWebServer mockWebServer;

  private ContactService contactService;

  private ObjectMapper objectMapper = new ObjectMapper();
  @Mock
  private IHubSpotService hubSpotService;

  @Test
  void createContact() throws InterruptedException, JsonProcessingException {
    ContactDTO contactDTO = new ContactDTO(
      "test@test.com", "Test", "T"
    );

    mockWebServer.enqueue(new MockResponse.Builder()
        .code(200)
        .body("")
      .build()
    );

    ContactDTO result = contactService.createContact(contactDTO);

    assertNotNull(result);
    assertEquals("Test", result.firstname());

    var recordedRequest = mockWebServer.takeRequest();
    assertEquals("/crm/v3/objects/contacts", recordedRequest.getPath());
    assertEquals("POST", recordedRequest.getMethod());
    String body = recordedRequest.getBody().readUtf8();

    Map<String, Object> sentJson = objectMapper.readValue(body, Map.class);
    assertTrue(sentJson.containsKey("properties"));
  }

  @Test
  void createContactWithRetry() throws InterruptedException, JsonProcessingException {
    ContactDTO contactDTO = new ContactDTO(
      "test@test.com", "Test", "T"
    );

    mockWebServer.enqueue(new MockResponse.Builder()
      .code(429)
      .body("")
      .build()
    );

    mockWebServer.enqueue(new MockResponse.Builder()
      .code(200)
      .body("")
      .build()
    );

    ContactDTO result = contactService.createContact(contactDTO);

    assertNotNull(result);
    assertEquals("Test", result.firstname());

    var recordedRequest = mockWebServer.takeRequest();
    assertEquals("/crm/v3/objects/contacts", recordedRequest.getPath());
    assertEquals("POST", recordedRequest.getMethod());
    String body = recordedRequest.getBody().readUtf8();

    Map<String, Object> sentJson = objectMapper.readValue(body, Map.class);
    assertTrue(sentJson.containsKey("properties"));
  }

  @BeforeEach
  void initialize() {
    WebClient mockedWebClient = WebClient.builder()
      .baseUrl(mockWebServer.url("/").toString())
      .build();
    contactService = new ContactService(mockedWebClient, hubSpotService );
  }

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }
}