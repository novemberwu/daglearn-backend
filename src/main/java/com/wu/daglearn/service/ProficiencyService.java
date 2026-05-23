package com.wu.daglearn.service;

import com.wu.daglearn.dto.AnswerSubmissionDto;
import com.wu.daglearn.dto.AttemptResultDto;
import com.wu.daglearn.dto.ConceptProficiencyDto;
import com.wu.daglearn.model.*;
import com.wu.daglearn.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProficiencyService {

    private final UserRepository userRepository;
    private final McqResourceRepository mcqResourceRepository;
    private final ConceptRepository conceptRepository;
    private final TopicRepository topicRepository;

    public ProficiencyService(UserRepository userRepository, 
                              McqResourceRepository mcqResourceRepository, 
                              ConceptRepository conceptRepository,
                              TopicRepository topicRepository) {
        this.userRepository = userRepository;
        this.mcqResourceRepository = mcqResourceRepository;
        this.conceptRepository = conceptRepository;
        this.topicRepository = topicRepository;
    }

    public List<ConceptProficiencyDto> getUserProficiencies(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return user.getProficiencies().stream()
                .map(p -> new ConceptProficiencyDto(
                        p.getConcept().getId(),
                        p.getConcept().getName(),
                        p.getPercentage()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public AttemptResultDto submitAnswer(String userId, AnswerSubmissionDto submission) {
        McqResource mcq = mcqResourceRepository.findById(submission.getMcqId())
                .orElseThrow(() -> new RuntimeException("MCQ not found: " + submission.getMcqId()));

        boolean isCorrect = mcq.getCorrectAnswer().equalsIgnoreCase(submission.getAnswer());
        int score = isCorrect ? 1 : 0;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 1. Record/Update Resource Attempt
        updateResourceAttempt(user, mcq, score);

        // 2. Find Parent Concept and Recalculate Proficiency
        Concept conceptNode = conceptRepository.findByResourceId(mcq.getId())
                .orElseThrow(() -> new RuntimeException("Parent concept not found for resource: " + mcq.getId()));
        
        // Re-fetch to ensure relationships (resources) are loaded
        Concept concept = conceptRepository.findById(conceptNode.getId()).orElseThrow();

        // Step 2 in data_model.md: (all score / all attempt)
        double newPercentage = calculateConceptProficiency(user, concept);
        updateConceptProficiency(user, concept, newPercentage);

        // 3. Roll up to Topic level (Step 3 in data_model.md)
        List<Topic> parentTopics = topicRepository.findTopicsByConceptId(concept.getId());
        String masteredTopicId = null;

        for (Topic topicNode : parentTopics) {
            Topic topic = topicRepository.findById(topicNode.getId()).orElseThrow();
            if (checkIfTopicMastered(user, topic)) {
                updateTopicMastery(user, topic);
                masteredTopicId = topic.getId();
            }
        }

        userRepository.save(user);

        return new AttemptResultDto(isCorrect, concept.getId(), newPercentage, masteredTopicId, masteredTopicId != null ? 100.0 : 0.0);
    }

    private void updateResourceAttempt(User user, Resource resource, int score) {
        // data_model.md Step 2: "all score / all attempt"
        // We always add a new attempt record to the history
        user.getAttempts().add(new ResourceAttempt(resource, score));
    }

    private double calculateConceptProficiency(User user, Concept concept) {
        // Step 2: (all score / all attempt) for resources of this concept
        List<String> conceptResourceIds = concept.getResources().stream()
                .map(Resource::getId)
                .collect(Collectors.toList());

        long totalAttempts = user.getAttempts().stream()
                .filter(a -> conceptResourceIds.contains(a.getResource().getId()))
                .count();
        
        if (totalAttempts == 0) return 0.0;

        long totalScore = user.getAttempts().stream()
                .filter(a -> conceptResourceIds.contains(a.getResource().getId()))
                .mapToLong(ResourceAttempt::getScore)
                .sum();

        return (double) totalScore / totalAttempts * 100.0;
    }

    private void updateConceptProficiency(User user, Concept concept, double percentage) {
        Optional<ConceptProficiency> existing = user.getProficiencies().stream()
                .filter(p -> p.getConcept().getId().equals(concept.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setPercentage(percentage);
        } else {
            user.getProficiencies().add(new ConceptProficiency(concept, percentage));
        }
    }

    private boolean checkIfTopicMastered(User user, Topic topic) {
        Set<Concept> topicConcepts = topic.getConcepts();
        if (topicConcepts.isEmpty()) return false;

        // Step 3: For all concepts under this topic, user understand percentage is > 90%
        for (Concept tc : topicConcepts) {
            double conceptProficiency = user.getProficiencies().stream()
                    .filter(p -> p.getConcept().getId().equals(tc.getId()))
                    .map(ConceptProficiency::getPercentage)
                    .findFirst()
                    .orElse(0.0);
            
            if (conceptProficiency <= 90.0) {
                return false;
            }
        }
        return true;
    }

    private void updateTopicMastery(User user, Topic topic) {
        boolean alreadyMastered = user.getTopicProficiencies().stream()
                .anyMatch(p -> p.getTopic().getId().equals(topic.getId()));

        if (!alreadyMastered) {
            user.getTopicProficiencies().add(new TopicProficiency(topic));
        }
    }
}
