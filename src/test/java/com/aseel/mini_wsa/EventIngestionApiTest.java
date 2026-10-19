package com.aseel.mini_wsa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventIngestionApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateEventAndReturnExistingOnRetry() throws Exception {
        // Prepare event, send twice, and verify responses
        String eventId = "integration-" + UUID.randomUUID();
        String json = """
            {
            "eventId": "%s",
            "timestamp": "2026-10-19T16:00:00Z",
            "configId": 14227,
            "clientIp": "203.0.113.50",
            "path": "/api/login",
            "action": "DENY",
            "rule": {
                "severity": "HIGH",
                "category": "INJECTION"
            }
            }
            """.formatted(eventId);
           
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/v1/events/ingest"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> first =client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, first.statusCode());

            JsonNode firstBody = objectMapper.readTree(first.body());

            assertEquals(1, firstBody.get("created").asInt());
            assertEquals(0, firstBody.get("existing").asInt());
            assertEquals(0, firstBody.get("rejected").asInt());

            JsonNode firstEvent = firstBody.get("results").get(0).get("event");
            assertEquals(eventId, firstEvent.get("eventId").asString());
            assertEquals("SQL/Command Injection", firstEvent.get("attackType").asString());

            HttpResponse<String> second =client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, second.statusCode());

            JsonNode secondBody = objectMapper.readTree(second.body());

            assertEquals(0, secondBody.get("created").asInt());
            assertEquals(1, secondBody.get("existing").asInt());
            assertEquals(0, secondBody.get("rejected").asInt());

            JsonNode secondEvent = secondBody.get("results").get(0).get("event");
            assertEquals(firstEvent, secondEvent);

            
        }
    }
}