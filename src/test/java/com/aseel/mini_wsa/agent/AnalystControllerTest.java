package com.aseel.mini_wsa.agent;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web slice only: no database and no Anthropic call (AnalystService is mocked).
 */
@WebMvcTest(AnalystController.class)
class AnalystControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalystService analystService;

    @Test
    void returnsAnswerForValidQuestion() throws Exception {
        when(analystService.ask("Top attackers today?")).thenReturn("192.0.2.10 with 12 events.");

        mockMvc.perform(post("/v1/analyst/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"question": "Top attackers today?"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("192.0.2.10 with 12 events."));
    }

    @Test
    void rejectsBlankQuestionWithoutCallingModel() throws Exception {
        mockMvc.perform(post("/v1/analyst/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"question": "   "}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("question"))
                .andExpect(jsonPath("$.message").value("question must not be blank"));

        verify(analystService, never()).ask(anyString());
    }

    @Test
    void rejectsMissingQuestionField() throws Exception {
        mockMvc.perform(post("/v1/analyst/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("question"));

        verify(analystService, never()).ask(anyString());
    }

    @Test
    void rejectsTooLongQuestion() throws Exception {
        String question = "a".repeat(AnalystController.MAX_QUESTION_LENGTH + 1);

        mockMvc.perform(post("/v1/analyst/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\": \"" + question + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("question"));

        verify(analystService, never()).ask(anyString());
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/v1/analyst/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON or missing request body."));

        verify(analystService, never()).ask(anyString());
    }
}
