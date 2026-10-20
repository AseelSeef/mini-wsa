package com.aseel.mini_wsa.controller;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aseel.mini_wsa.dto.SummaryResponse;
import com.aseel.mini_wsa.service.EventStatisticsService;

@RestController
@RequestMapping("/v1/stats")
public class StatsController {

    private final EventStatisticsService eventStatisticsService;

    public StatsController(EventStatisticsService eventStatisticsService) {
        this.eventStatisticsService = eventStatisticsService;
    }

    @GetMapping("/summary")
    public SummaryResponse summary(
            @RequestParam(name = "configId", required = false) Integer configId,
            @RequestParam(name = "from") Instant from,
            @RequestParam(name = "to") Instant to) {

        return eventStatisticsService.summary(configId, from, to);
    }

}
