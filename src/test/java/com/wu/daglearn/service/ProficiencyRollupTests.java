package com.wu.daglearn.service;

import com.wu.daglearn.dto.AnswerSubmissionDto;
import com.wu.daglearn.dto.AttemptResultDto;
import com.wu.daglearn.model.*;
import com.wu.daglearn.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProficiencyRollupTests {

    @Autowired
    private ProficiencyService proficiencyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private static final String USER_ID = "tester@rollup.com";
    private static final String TOPIC_ID = "T-ROLLUP";
    private static final String CONCEPT_1_ID = "C-1";
    private static final String CONCEPT_2_ID = "C-2";
    private static final String RESOURCE_1_ID = "R-1";
    private static final String RESOURCE_2_ID = "R-2";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();

        // Setup a standard Topic -> Concept -> Resource hierarchy
        Topic topic = new Topic(TOPIC_ID, "Rollup Topic");
        
        Concept c1 = new Concept(CONCEPT_1_ID, "Concept 1");
        McqResource r1 = new McqResource(RESOURCE_1_ID, "Q1", List.of("A", "B"), "A");
        c1.getResources().add(r1);
        
        Concept c2 = new Concept(CONCEPT_2_ID, "Concept 2");
        McqResource r2 = new McqResource(RESOURCE_2_ID, "Q2", List.of("A", "B"), "A");
        c2.getResources().add(r2);

        topic.getConcepts().addAll(Set.of(c1, c2));

        resourceRepository.saveAll(List.of(r1, r2));
        conceptRepository.saveAll(List.of(c1, c2));
        topicRepository.save(topic);

        User user = new User(USER_ID, "Tester", USER_ID);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Single Correct Answer: Concept 100%, Topic NOT Mastered (waiting for C2)")
    void testPartialMastery() {
        AttemptResultDto result = proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A"));
        
        assertTrue(result.isCorrect());
        assertEquals(100.0, result.getNewProficiencyPercentage());
        assertNull(result.getTopicId(), "Topic should not be mastered because C2 is 0%");
    }

    @Test
    @DisplayName("Threshold Test: Exactly 90% should NOT master topic")
    void testThresholdExactly90() {
        // C1: 1 correct = 100%
        proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A"));
        
        // C2: 9 correct, 1 wrong = 90%
        for (int i = 0; i < 9; i++) {
            proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_2_ID, "A"));
        }
        AttemptResultDto result = proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_2_ID, "B")); // Wrong
        
        assertEquals(90.0, result.getNewProficiencyPercentage());
        assertNull(result.getTopicId(), "Topic should not be mastered at exactly 90% proficiency");
    }

    @Test
    @DisplayName("Threshold Test: 91% should master topic if all concepts are > 90%")
    void testThresholdAbove90() {
        // C1: 1 correct = 100%
        proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A"));
        
        // C2: 10 correct, 1 wrong = ~90.9%
        for (int i = 0; i < 10; i++) {
            proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_2_ID, "A"));
        }
        AttemptResultDto result = proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_2_ID, "B")); // Wrong
        
        assertTrue(result.getNewProficiencyPercentage() > 90.0);
        assertEquals(TOPIC_ID, result.getTopicId(), "Topic should be mastered when all concepts > 90%");
    }

    @Test
    @DisplayName("Multiple Concepts: Both must pass 90% threshold")
    void testFullMasteryMultipleConcepts() {
        proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A")); // C1 -> 100%
        AttemptResultDto result = proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_2_ID, "A")); // C2 -> 100%
        
        assertEquals(TOPIC_ID, result.getTopicId());
        
        User user = userRepository.findById(USER_ID).orElseThrow();
        assertEquals(1, user.getTopicProficiencies().size());
        assertEquals(TOPIC_ID, user.getTopicProficiencies().get(0).getTopic().getId());
    }

    @Test
    @DisplayName("History Test: (all score / all attempt) cumulative calculation")
    void testCumulativeAttempts() {
        // 1 correct, 1 wrong = 50%
        proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A"));
        AttemptResultDto result = proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "B"));
        
        assertEquals(50.0, result.getNewProficiencyPercentage(), "Should be (1+0)/2 = 50%");
        
        // Add 9 more correct = 10/11 = 90.9%
        for (int i = 0; i < 9; i++) {
            result = proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A"));
        }
        assertTrue(result.getNewProficiencyPercentage() > 90.0);
    }

    @Test
    @DisplayName("Prerequisite Unlocking: Topic A mastery unlocks Topic B")
    void testUnlockingLogic() {
        // Setup Prereq: Topic B (DEPENDENT) -> Topic A (REQUIRED)
        Topic topicA = topicRepository.findById(TOPIC_ID).orElseThrow();
        Topic topicB = new Topic("T-B", "Topic B");
        topicB.getPrerequisites().add(topicA);
        topicRepository.save(topicB);

        // Initially Topic B should be locked
        List<Topic> unlocked = topicRepository.findUnlockedTopicsForUser(USER_ID);
        assertFalse(unlocked.stream().anyMatch(t -> t.getId().equals("T-B")));

        // Master Topic A (C1 and C2 > 90%)
        proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_1_ID, "A"));
        proficiencyService.submitAnswer(USER_ID, new AnswerSubmissionDto(RESOURCE_2_ID, "A"));

        // Now Topic B should be unlocked
        unlocked = topicRepository.findUnlockedTopicsForUser(USER_ID);
        assertTrue(unlocked.stream().anyMatch(t -> t.getId().equals("T-B")), "Topic B should be unlocked after Topic A is mastered");
    }
}
