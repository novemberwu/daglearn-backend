package com.wu.daglearn.service;

import com.wu.daglearn.dto.McqDto;
import com.wu.daglearn.model.McqResource;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.repository.McqResourceRepository;
import com.wu.daglearn.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final McqResourceRepository mcqResourceRepository;
    private final TopicRepository topicRepository;

    public TopicService(McqResourceRepository mcqResourceRepository, TopicRepository topicRepository) {
        this.mcqResourceRepository = mcqResourceRepository;
        this.topicRepository = topicRepository;
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
}
