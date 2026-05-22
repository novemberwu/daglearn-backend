package com.wu.daglearn.controller;

import com.wu.daglearn.dto.AnswerSubmissionDto;
import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User testUser = new User("tester@example.com", "tester", "tester@example.com");
        userRepository.save(testUser);
    }

    @Test
    void shouldSubmitCorrectAnswerAndIncreaseProficiency() throws Exception {
        // MCQ R-1: "What happens if you lose the head pointer?"
        // Correct Answer: "The entire list becomes unreachable (Memory Leak)"
        
        AnswerSubmissionDto submission = new AnswerSubmissionDto("R-1", "The entire list becomes unreachable (Memory Leak)");

        mockMvc.perform(post("/api/users/tester@example.com/attempts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)))
                .andExpect(jsonPath("$.conceptId", is("C-1")))
                // Concept C-1 has 1 resource (R-1). Correct answer should yield 100%
                .andExpect(jsonPath("$.newProficiencyPercentage", is(100.0)));
    }

    @Test
    void shouldSubmitWrongAnswerAndRecordLowProficiency() throws Exception {
        AnswerSubmissionDto submission = new AnswerSubmissionDto("R-1", "Nothing happens");

        mockMvc.perform(post("/api/users/tester@example.com/attempts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(false)))
                .andExpect(jsonPath("$.newProficiencyPercentage", is(0.0)));
    }
}
