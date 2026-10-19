package com.aseel.mini_wsa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aseel.mini_wsa.dto.IngestionResponse;
import com.aseel.mini_wsa.service.EventIngestionService;

import tools.jackson.databind.JsonNode;


@RestController
@RequestMapping("/v1/events")
public class EventController {

    private final EventIngestionService eventIngestionService;

    public EventController(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
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

    

}
