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
import java.util.stream.Collectors;

@Service
public class ProficiencyService {

    private final UserRepository userRepository;
    private final McqResourceRepository mcqResourceRepository;
    private final ConceptRepository conceptRepository;

    public ProficiencyService(UserRepository userRepository, 
                              McqResourceRepository mcqResourceRepository, 
                              ConceptRepository conceptRepository) {
        this.userRepository = userRepository;
        this.mcqResourceRepository = mcqResourceRepository;
        this.conceptRepository = conceptRepository;
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

        double newPercentage = calculateConceptProficiency(user, concept);
        updateConceptProficiency(user, concept, newPercentage);

        userRepository.save(user);

        return new AttemptResultDto(isCorrect, concept.getId(), newPercentage);
    }

    private void updateResourceAttempt(User user, Resource resource, int score) {
        Optional<ResourceAttempt> existing = user.getAttempts().stream()
                .filter(a -> a.getResource().getId().equals(resource.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setScore(score);
        } else {
            user.getAttempts().add(new ResourceAttempt(resource, score));
        }
    }

    private double calculateConceptProficiency(User user, Concept concept) {
        // Total resources associated with this concept
        // Note: Concept.getResources() should be loaded
        long totalResources = concept.getResources().size();
        if (totalResources == 0) return 0.0;

        // Correct attempts by user for these specific resources
        long correctAttempts = user.getAttempts().stream()
                .filter(a -> a.getScore() == 1)
                .filter(a -> concept.getResources().stream()
                        .anyMatch(r -> r.getId().equals(a.getResource().getId())))
                .count();

        return (double) correctAttempts / totalResources * 100.0;
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
}
