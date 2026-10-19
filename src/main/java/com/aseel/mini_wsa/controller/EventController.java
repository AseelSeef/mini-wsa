package com.aseel.mini_wsa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aseel.mini_wsa.service.EventIngestionService;


@RestController
@RequestMapping("/v1/events")
public class EventController {

    private final EventIngestionService eventIngestionService;

    public EventController(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
    }

    

}
