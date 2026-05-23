package com.wu.daglearn.controller;

import com.wu.daglearn.dto.McqDto;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topic API", description = "Endpoints for managing and retrieving topic-related information.")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    @Operation(summary = "Get all topics", description = "Retrieves all topics in the knowledge graph, including their prerequisite relationships.")
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/{topicId}/mcqs")
    @Operation(summary = "Get MCQs for a topic", description = "Retrieves all multiple-choice questions associated with the specified topic. Correct answers are excluded.")
    public ResponseEntity<List<McqDto>> getMcqsForTopic(
            @Parameter(description = "The ID of the topic (e.g., T-2)") @PathVariable String topicId) {
        List<McqDto> mcqs = topicService.getMcqsForTopic(topicId);
        return ResponseEntity.ok(mcqs);
    }

    @GetMapping("/unlocked/{userId}")
    @Operation(summary = "Get unlocked topics for user", description = "Retrieves topics that the user is ready to learn (all prerequisites mastered).")
    public ResponseEntity<List<Topic>> getUnlockedTopics(@PathVariable String userId) {
        return ResponseEntity.ok(topicService.getUnlockedTopicsForUser(userId));
    }
}
