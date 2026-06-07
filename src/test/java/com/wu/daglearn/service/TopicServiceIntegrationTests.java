package com.wu.daglearn.service;

import com.wu.daglearn.model.*;
import com.wu.daglearn.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TopicServiceIntegrationTests {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private DocumentResourceRepository documentResourceRepository;

    @Autowired
    private McqResourceRepository mcqResourceRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String TOPIC_ID = "T-SERVICE-TEST";
    private static final String CONCEPT_ID = "C-SERVICE-TEST";
    private static final String DOC_ID = "R-DOC-TEST";
    private static final String MCQ_ID = "R-MCQ-TEST";

    @BeforeEach
    void setUp() {
        // Enforce integration test determinism by cleaning up first
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();

        // Given: Prepare data model entities
        Topic topic = new Topic(TOPIC_ID, "Service Integration Testing");
        topic.setDescription("A test topic for checking repository and service methods.");
        topic.setRequiredProficiencyScore(85);

        Concept concept = new Concept(CONCEPT_ID, "Testing Concept");
        concept.setDescription("A specific concept under test.");

        DocumentResource documentResource = new DocumentResource(DOC_ID, "This is a detailed markdown integration document content.");
        
        McqResource mcqResource = new McqResource(MCQ_ID, "Which framework is used for testing?",
                List.of("JUnit", "None", "Other", "Unknown"), "JUnit", "JUnit is the standard testing framework.");

        // Link entities
        concept.getResources().add(documentResource);
        concept.getResources().add(mcqResource);
        topic.getConcepts().add(concept);

        // Save in correct order
        documentResourceRepository.save(documentResource);
        mcqResourceRepository.save(mcqResource);
        conceptRepository.save(concept);
        topicRepository.save(topic);
    }

    @Test
    @DisplayName("Given a topic exists, when findTopicById is called, then it returns the topic wrapped in an Optional")
    void givenTopicExists_whenFindTopicById_thenReturnTopicOptional() {
        // When
        Optional<Topic> topicOpt = topicRepository.findTopicById(TOPIC_ID);

        // Then
        assertTrue(topicOpt.isPresent(), "The topic should be found and present");
        Topic topic = topicOpt.get();
        assertEquals(TOPIC_ID, topic.getId());
        assertEquals("Service Integration Testing", topic.getTitle());
        assertEquals(85, topic.getRequiredProficiencyScore());
    }

    @Test
    @DisplayName("Given a topic exists and has concepts, when getConceptsByTopicId is called, then it returns the list of concepts")
    void givenTopicContainsConcepts_whenGetConceptsByTopicId_thenReturnConceptList() {
        // When
        List<Concept> concepts = topicService.getConceptsByTopicId(TOPIC_ID);

        // Then
        assertNotNull(concepts, "The concepts list should not be null");
        assertEquals(1, concepts.size(), "There should be exactly one concept");
        Concept concept = concepts.get(0);
        assertEquals(CONCEPT_ID, concept.getId());
        assertEquals("Testing Concept", concept.getName());
    }

    @Test
    @DisplayName("Given a concept has a document resource, when getDocumentResourcesByConceptId is called, then it returns the list of document resources")
    void givenConceptHasDocumentResource_whenGetDocumentResourcesByConceptId_thenReturnDocumentResourceList() {
        // When
        List<DocumentResource> docs = topicService.getDocumentResourcesByConceptId(CONCEPT_ID);

        // Then
        assertNotNull(docs, "The documents list should not be null");
        assertEquals(1, docs.size(), "There should be exactly one document resource");
        DocumentResource doc = docs.get(0);
        assertEquals(DOC_ID, doc.getId());
        assertEquals("DOCUMENT", doc.getType());
        assertEquals("This is a detailed markdown integration document content.", doc.getContent());
    }

    @Test
    @DisplayName("Given a concept has an MCQ resource, when getMcqResourcesByConceptId is called, then it returns the list of MCQ resources")
    void givenConceptHasMcqResource_whenGetMcqResourcesByConceptId_thenReturnMcqResourceList() {
        // When
        List<McqResource> mcqs = topicService.getMcqResourcesByConceptId(CONCEPT_ID);

        // Then
        assertNotNull(mcqs, "The MCQ list should not be null");
        assertEquals(1, mcqs.size(), "There should be exactly one MCQ resource");
        McqResource mcq = mcqs.get(0);
        assertEquals(MCQ_ID, mcq.getId());
        assertEquals("MCQ", mcq.getType());
        assertEquals("Which framework is used for testing?", mcq.getContent());
        assertEquals("JUnit", mcq.getCorrectAnswer());
        assertEquals("JUnit is the standard testing framework.", mcq.getExplanation());
        assertEquals(4, mcq.getOptions().size());
    }
}
