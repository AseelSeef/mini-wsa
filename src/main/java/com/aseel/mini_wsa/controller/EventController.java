package com.aseel.mini_wsa.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aseel.mini_wsa.dto.IngestionResponse;
import com.aseel.mini_wsa.dto.SamplesResponse;
import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.service.EventIngestionService;
import com.aseel.mini_wsa.service.EventSamplesService;

import tools.jackson.databind.JsonNode;


@RestController
@RequestMapping("/v1/events")
public class EventController {

    private final EventIngestionService eventIngestionService;
    private final EventSamplesService eventSamplesService;

    public EventController(EventIngestionService eventIngestionService,EventSamplesService eventSamplesService) {
        this.eventIngestionService = eventIngestionService;
        this.eventSamplesService = eventSamplesService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<IngestionResponse> ingest(@RequestBody JsonNode input) {
        IngestionResponse response = eventIngestionService.ingest(input);

        int status;
        if (response.getCreated() == response.getResults().size()) {
            status = 201;
        } else if (response.getRejected() == response.getResults().size()) {
            status = 400;
        } else {
            status = 200;
        }

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/samples")
    public SamplesResponse samples(
            @RequestParam(name = "configId", required = false) Integer configId,
            @RequestParam(name = "from", required = false) Instant from,
            @RequestParam(name = "to", required = false) Instant to,
            @RequestParam(name = "category", required = false) AttackCategory category,
            @RequestParam(name = "action", required = false) Action action,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset) {

        return eventSamplesService.samples(
            configId, from, to, category, action, limit, offset
        );
    }

    

}
