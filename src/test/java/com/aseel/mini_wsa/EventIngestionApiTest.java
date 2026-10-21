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

import com.aseel.mini_wsa.repository.SecurityEventRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventIngestionApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityEventRepository repository;

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


    @Test
    void shouldGroupTimeSeriesAndExcludeUpperBoundary() throws Exception {

        String firstId = "timeseries-" + UUID.randomUUID();
        String secondId = "timeseries-" + UUID.randomUUID();
        int configId = -java.util.concurrent.ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

        String json = """
                [
                  {
                    "eventId": "%s",
                    "timestamp": "2040-01-01T10:04:59Z",
                    "configId": %d,
                    "clientIp": "198.51.100.10",
                    "path": "/api/login",
                    "action": "DENY",
                    "rule": {
                      "severity": "HIGH",
                      "category": "BOT"
                    }
                  },
                  {
                    "eventId": "%s",
                    "timestamp": "2040-01-01T10:05:00Z",
                    "configId": %d,
                    "clientIp": "198.51.100.10",
                    "path": "/api/login",
                    "action": "DENY",
                    "rule": {
                      "severity": "HIGH",
                      "category": "BOT"
                    }
                  }
                ]
                """.formatted(firstId, configId, secondId, configId);

        String baseUrl = "http://localhost:" + port;

        try (HttpClient client = HttpClient.newHttpClient()) {
            // Arrange: create two events on opposite sides of a bucket boundary.
            HttpRequest ingestRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/events/ingest"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> ingestResponse = client.send(
                    ingestRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, ingestResponse.statusCode());

            // Act: query both five-minute buckets.
            String query = baseUrl + "/v1/stats/timeseries"
                    + "?configId=" + configId
                    + "&from=2040-01-01T10:00:00Z"
                    + "&to=2040-01-01T10:10:00Z"
                    + "&interval=5m";

            HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder().uri(URI.create(query)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            JsonNode buckets = objectMapper.readTree(response.body())
                    .get("buckets");

            // Assert: exactly one event in each correctly aligned bucket.
            assertEquals(2, buckets.size());

            assertEquals(
                    "2040-01-01T10:00:00Z",
                    buckets.get(0).get("timestamp").asString());
            assertEquals(1L, buckets.get(0).get("count").asLong());

            assertEquals(
                    "2040-01-01T10:05:00Z",
                    buckets.get(1).get("timestamp").asString());
            assertEquals(1L, buckets.get(1).get("count").asLong());

            // The event exactly at 'to' must be excluded.
            String boundaryQuery = query.replace(
                    "to=2040-01-01T10:10:00Z",
                    "to=2040-01-01T10:05:00Z");

            HttpResponse<String> boundaryResponse = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(boundaryQuery))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, boundaryResponse.statusCode());

            JsonNode boundaryBuckets = objectMapper.readTree(boundaryResponse.body()).get("buckets");

            assertEquals(1, boundaryBuckets.size());
            assertEquals(
                    "2040-01-01T10:00:00Z",
                    boundaryBuckets.get(0).get("timestamp").asString());
            assertEquals(1L, boundaryBuckets.get(0).get("count").asLong());
        } finally {
            repository.deleteAllById(List.of(firstId, secondId));
        }
    }
}