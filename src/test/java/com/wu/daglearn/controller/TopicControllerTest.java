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
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();

        // Topic hierarchy: Arrays (T-1) -> Linked Lists (T-2)
        Topic arrays = new Topic("T-1", "Arrays");
        Topic linkedLists = new Topic("T-2", "Linked Lists");
        linkedLists.getPrerequisites().add(arrays);

        Concept arrayConcept = new Concept("C-ARR", "Array Concept");
        McqResource arrayMcq = new McqResource("R-ARR", "Array Q", List.of("A", "B"), "A");
        arrayConcept.getResources().add(arrayMcq);
        arrays.getConcepts().add(arrayConcept);

        Concept llConcept = new Concept("C-LL", "LL Concept");
        McqResource llMcq = new McqResource("R-LL", "LL Q", List.of("A", "B"), "A");
        llConcept.getResources().add(llMcq);
        linkedLists.getConcepts().add(llConcept);

        resourceRepository.saveAll(List.of(arrayMcq, llMcq));
        conceptRepository.saveAll(List.of(arrayConcept, llConcept));
        topicRepository.saveAll(Set.of(arrays, linkedLists));
    }

    @Test
    void shouldReturnMcqsForTopicWithoutCorrectAnswer() throws Exception {
        mockMvc.perform(get("/api/topics/T-2/mcqs")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_topic:read")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].correctAnswer").doesNotExist());
    }

    @Test
    void shouldReturnUnlockedTopicsForUser() throws Exception {
        User user = new User("pathfinder@example.com", "pathfinder", "pathfinder@example.com");
        
        // 1. Give user mastery of Arrays (T-1)
        Topic arrays = topicRepository.findById("T-1").orElseThrow();
        user.getTopicProficiencies().add(new TopicProficiency(arrays));
        userRepository.save(user);

        // 2. Query unlocked topics. Linked Lists (T-2) should be returned because its prereq (T-1) is mastered.
        mockMvc.perform(get("/api/topics/unlocked/pathfinder@example.com")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_topic:read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("T-2")));
    }
}
