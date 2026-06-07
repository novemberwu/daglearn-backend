package com.wu.daglearn.service;

import com.wu.daglearn.dto.McqDto;
import com.wu.daglearn.model.Concept;
import com.wu.daglearn.model.DocumentResource;
import com.wu.daglearn.model.McqResource;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.repository.ConceptRepository;
import com.wu.daglearn.repository.DocumentResourceRepository;
import com.wu.daglearn.repository.McqResourceRepository;
import com.wu.daglearn.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final McqResourceRepository mcqResourceRepository;
    private final TopicRepository topicRepository;
    private final DocumentResourceRepository documentResourceRepository;
    private final ConceptRepository conceptRepository;

    public TopicService(McqResourceRepository mcqResourceRepository,
                        TopicRepository topicRepository,
                        DocumentResourceRepository documentResourceRepository,
                        ConceptRepository conceptRepository) {
        this.mcqResourceRepository = mcqResourceRepository;
        this.topicRepository = topicRepository;
        this.documentResourceRepository = documentResourceRepository;
        this.conceptRepository = conceptRepository;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public List<Topic> getUnlockedTopicsForUser(String userId) {
        return topicRepository.findUnlockedTopicsForUser(userId);
    }

    public List<McqDto> getMcqsForTopic(String topicId) {
        List<McqResource> mcqs = mcqResourceRepository.findAllByTopicId(topicId);
        return mcqs.stream()
                .map(mcq -> new McqDto(mcq.getId(), mcq.getContent(), mcq.getOptions()))
                .collect(Collectors.toList());
    }

    public List<Concept> getConceptsByTopicId(String topicId) {
        return conceptRepository.findConceptsByTopicId(topicId);
    }

    public List<DocumentResource> getDocumentResourcesByConceptId(String conceptId) {
        return documentResourceRepository.findByConceptId(conceptId);
    }

    public List<McqResource> getMcqResourcesByConceptId(String conceptId) {
        return mcqResourceRepository.findByConceptId(conceptId);
    }
}
