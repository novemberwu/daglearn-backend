package com.wu.daglearn.controller;

import com.wu.daglearn.dto.AnswerSubmissionDto;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.model.TopicProficiency;
import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.TopicRepository;
import com.wu.daglearn.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
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

    @Test
    void shouldReturnMcqsForTopicWithoutCorrectAnswer() throws Exception {
        mockMvc.perform(get("/api/topics/T-2/mcqs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].correctAnswer").doesNotExist());
    }

    @Test
    void shouldReturnUnlockedTopicsForUser() throws Exception {
        // Topic hierarchy in DataLoader: Arrays (T-1) -> Linked Lists (T-2) -> Trees (T-3) -> Graphs (T-4)
        
        userRepository.deleteAll();
        User user = new User("pathfinder@example.com", "pathfinder", "pathfinder@example.com");
        
        // 1. Give user mastery of Arrays (T-1)
        Topic arrays = topicRepository.findById("T-1").orElseThrow();
        user.getTopicProficiencies().add(new TopicProficiency(arrays, 100.0));
        userRepository.save(user);

        // 2. Query unlocked topics. Linked Lists (T-2) should be returned because its prereq (T-1) is mastered.
        // Note: Linked Lists threshold is 80 in our script.
        mockMvc.perform(get("/api/topics/unlocked/pathfinder@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].id", hasItem("T-2")));
    }
}
