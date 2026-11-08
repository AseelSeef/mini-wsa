package com.aseel.mini_wsa.agent;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/v1/analyst")
public class AnalystController {

    static final int MAX_QUESTION_LENGTH = 2000;

    private final AnalystService analystService;

    public AnalystController(AnalystService analystService) {
        this.analystService = analystService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return new AskResponse(analystService.ask(request.question()));
    }

    public record AskRequest(
            @NotBlank(message = "question must not be blank")
            @Size(max = MAX_QUESTION_LENGTH, message = "question must be at most " + MAX_QUESTION_LENGTH + " characters")
            String question) {
    }

    public record AskResponse(String answer) {
    }
}
