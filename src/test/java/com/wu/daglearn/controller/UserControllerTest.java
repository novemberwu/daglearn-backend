package com.wu.daglearn.controller;

import com.wu.daglearn.dto.AnswerSubmissionDto;
import com.wu.daglearn.model.*;
import com.wu.daglearn.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();

        // Create Test Data
        Topic topic = new Topic("T-TEST", "Test Topic");
        Concept concept = new Concept("C-1", "Test Concept");
        McqResource mcq = new McqResource("R-1", "What happens if you lose the head pointer?", 
                List.of("The entire list becomes unreachable (Memory Leak)", "Nothing happens"), 
                "The entire list becomes unreachable (Memory Leak)");

        concept.getResources().add(mcq);
        topic.getConcepts().add(concept);

        resourceRepository.save(mcq);
        conceptRepository.save(concept);
        topicRepository.save(topic);

        User testUser = new User("tester@example.com", "tester", "tester@example.com");
        userRepository.save(testUser);
    }

    @Test
    void shouldSubmitCorrectAnswerAndIncreaseProficiency() throws Exception {
        AnswerSubmissionDto submission = new AnswerSubmissionDto("R-1", "The entire list becomes unreachable (Memory Leak)");

        mockMvc.perform(post("/api/users/tester@example.com/attempts")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_topic:read")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)))
                .andExpect(jsonPath("$.conceptId", is("C-1")))
                .andExpect(jsonPath("$.newProficiencyPercentage", is(100.0)))
                .andExpect(jsonPath("$.topicId", is("T-TEST")))
                .andExpect(jsonPath("$.topicProficiencyPercentage", is(100.0)));
    }

    @Test
    void shouldSubmitWrongAnswerAndRecordLowProficiency() throws Exception {
        AnswerSubmissionDto submission = new AnswerSubmissionDto("R-1", "Nothing happens");

        mockMvc.perform(post("/api/users/tester@example.com/attempts")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_topic:read")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(false)))
                .andExpect(jsonPath("$.newProficiencyPercentage", is(0.0)))
                .andExpect(jsonPath("$.topicProficiencyPercentage", is(0.0)));
    }
}
