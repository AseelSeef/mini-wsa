package com.aseel.mini_wsa.generator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.GeoLocation;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.model.SecurityRule;
import com.aseel.mini_wsa.model.Severity;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class SecurityEventGenerator {

    private static final String[] PATHS = {
        "/api/login", "/admin/users", "/api/products",
        "/search", "/checkout", "/api/accounts"
    };

    private static final Instant START =
        Instant.parse("2026-10-20T10:00:00Z");

    public static void main(String[] args) throws Exception {
        int count = args.length > 0 ? Integer.parseInt(args[0]) : 10_000;
        Path output = Path.of(args.length > 1 ? args[1] : "events.json");
        long seed = args.length > 2 ? Long.parseLong(args[2]) : 42L;

        if (count < 1) {
            throw new IllegalArgumentException("Event count must be positive.");
        }

        List<SecurityEvent> events = generate(count, seed);
        ObjectMapper mapper = JsonMapper.builder().build();

        Files.writeString(
            output,
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events),
            StandardCharsets.UTF_8
        );

        System.out.printf(
            "Generated %d events in %s%n",
            events.size(), output.toAbsolutePath()
        );
    }

    public static List<SecurityEvent> generate(int count, long seed) {
        if (count < 1) {
            throw new IllegalArgumentException("Event count must be positive.");
        }

        Random random = new Random(seed);
        List<SecurityEvent> events = new ArrayList<>(count);

        AttackCategory[] categories = AttackCategory.values();
        Severity[] severities = Severity.values();
        Action[] actions = Action.values();

        for (int i = 0; i < count; i++) {
            // First 10 events in each block of 50 form an attack wave.
            boolean attackWave = i % 50 < 10;
            int waveId = i / 50;

            AttackCategory category = attackWave
                ? AttackCategory.BOT
                : categories[random.nextInt(categories.length)];

            Severity severity = attackWave
                ? Severity.HIGH
                : severities[random.nextInt(severities.length)];

            Action action = attackWave
                ? Action.DENY
                : actions[random.nextInt(actions.length)];

            SecurityEvent event = new SecurityEvent();
            event.setEventId("generated-" + seed + "-" + i);
            event.setTimestamp(START.plusSeconds(i));

            // Wave events share a configuration, IP and path.
            event.setConfigId(
                attackWave ? 14227 : 14227 + random.nextInt(3)
            );

            event.setClientIp(attackWave
                ? "198.51.100." + (1 + waveId % 254)
                : "203.0.113." + (1 + random.nextInt(254)));

            event.setPath(attackWave
                ? "/api/login"
                : PATHS[random.nextInt(PATHS.length)]);

            event.setPolicyId("pol-web-" + event.getConfigId());
            event.setHostname("app-" + event.getConfigId() + ".example.com");

            event.setMethod(
                event.getPath().contains("login") ? "POST" : "GET"
            );

            event.setStatusCode(action == Action.DENY ? 403 : 200);

            event.setUserAgent(attackWave
                ? "CredentialCheckBot/1.0"
                : "Mozilla/5.0");

            event.setRule(new SecurityRule(
                "rule-" + category.name(),
                category.name(),
                "Detected " + category.name() + " activity",
                severity,
                category
            ));

            event.setGeoLocation(attackWave
                ? new GeoLocation("US", "New York")
                : new GeoLocation("DE", "Berlin"));

            event.setRequestSize(100L + random.nextInt(4_000));
            event.setResponseSize(100L + random.nextInt(8_000));
            event.setAction(action);
            
            events.add(event);
        }

        events.sort(Comparator.comparing(SecurityEvent::getTimestamp));
        return events;
    }
}