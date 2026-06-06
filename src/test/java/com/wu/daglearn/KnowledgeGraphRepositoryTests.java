package com.wu.daglearn;

import com.wu.daglearn.model.Concept;
import com.wu.daglearn.model.McqResource;
import com.wu.daglearn.model.Resource;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.ConceptRepository;
import com.wu.daglearn.repository.ResourceRepository;
import com.wu.daglearn.repository.TopicRepository;
import com.wu.daglearn.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KnowledgeGraphRepositoryTests {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();

        // 1. Create Topics
        Topic arrays = new Topic("T-1", "Arrays");
        arrays.setDescription("Contiguous memory collection of elements.");

        Topic linkedLists = new Topic("T-2", "Linked Lists");
        linkedLists.setDescription("Linear collection of data elements whose order is not given by their physical placement in memory.");
        linkedLists.setRequiredProficiencyScore(80);

        // 2. Create Concept
        Concept nodeStructure = new Concept("C-1", "Node Structure");
        nodeStructure.setDescription("Understanding the data field and the next pointer.");

        // 3. Create Resource
        McqResource mcq1 = new McqResource("R-1", "What happens if you lose the head pointer?",
                List.of("The entire list becomes unreachable (Memory Leak)", "Only the head is lost", "Nothing happens", "The next pointer is updated"),
                "The entire list becomes unreachable (Memory Leak)");
        mcq1.setType("MCQ");

        // 4. Build Relationships
        nodeStructure.getResources().add(mcq1);
        linkedLists.getConcepts().add(nodeStructure);
        linkedLists.getPrerequisites().add(arrays);

        // 5. Save all
        resourceRepository.save(mcq1);
        conceptRepository.save(nodeStructure);
        topicRepository.saveAll(Set.of(arrays, linkedLists));
    }

    @Test
    void testLinkedListTopicRetrieval() {
        // 1. Verify Topic exists
        Optional<Topic> linkedListOpt = topicRepository.findById("T-2");
        assertTrue(linkedListOpt.isPresent(), "Linked List topic (T-2) should exist in DB");
        
        Topic linkedList = linkedListOpt.get();
        assertEquals("Linked Lists", linkedList.getTitle());
        assertEquals(80, linkedList.getRequiredProficiencyScore());
    }

    @Test
    void testTopicToConceptRelationship() {
        // 2. Verify Topic contains Concepts
        Topic linkedList = topicRepository.findById("T-2").orElseThrow();
        Set<Concept> concepts = linkedList.getConcepts();
        
        assertFalse(concepts.isEmpty(), "Linked List should have concepts");
        
        boolean hasNodeStructure = concepts.stream()
                .anyMatch(c -> c.getName().equals("Node Structure"));
        assertTrue(hasNodeStructure, "Should contain 'Node Structure' concept");
    }

    @Test
    void testConceptToResourceRelationship() {
        // 3. Verify Concepts link to Resources
        // We find the concept directly to ensure its repository works
        Optional<Concept> nodeStructureOpt = conceptRepository.findById("C-1");
        assertTrue(nodeStructureOpt.isPresent());
        
        Concept nodeStructure = nodeStructureOpt.get();
        Set<Resource> resources = nodeStructure.getResources();
        
        assertFalse(resources.isEmpty(), "Node Structure concept should have resources");
        
        Resource resource = resources.iterator().next();
        assertTrue(resource instanceof McqResource, "Resource should be an instance of McqResource");
        
        McqResource mcq = (McqResource) resource;
        assertEquals("MCQ", mcq.getType());
        assertTrue(mcq.getContent().contains("head pointer"), "MCQ content should match");
        
        List<String> options = mcq.getOptions();
        assertNotNull(options);
        assertEquals(4, options.size(), "Should have 4 options");
        assertEquals("The entire list becomes unreachable (Memory Leak)", mcq.getCorrectAnswer());
    }

    @Test
    void testPrerequisiteRelationship() {
        // 4. Verify DAG relationship (requires)
        // Note: In the script, T-2 REQUIRES T-1 (Arrays)
        Topic linkedList = topicRepository.findById("T-2").orElseThrow();
        Set<Topic> prerequisites = linkedList.getPrerequisites();
        
        boolean hasArrays = prerequisites.stream()
                .anyMatch(p -> p.getId().equals("T-1"));
        assertTrue(hasArrays, "Linked Lists should require Arrays");
    }

    @Test
    void testUserProficiencyTracking() {
        // 5. Create a User and record proficiency
        userRepository.deleteAll();
        
        User user = new User("rachel@example.com", "rachel", "rachel@example.com");
        
        // Link to a Resource (MCQ)
        McqResource mcq = (McqResource) resourceRepository.findById("R-1").orElseThrow();
        user.getAttempts().add(new com.wu.daglearn.model.ResourceAttempt(mcq, 1));
        
        // Link to a Concept
        Concept nodeStructure = conceptRepository.findById("C-1").orElseThrow();
        user.getProficiencies().add(new com.wu.daglearn.model.ConceptProficiency(nodeStructure, 95.0));
        
        userRepository.save(user);
        
        // Verify
        User retrievedUser = userRepository.findById("rachel@example.com").orElseThrow();
        assertEquals(1, retrievedUser.getAttempts().size());
        assertEquals(1, retrievedUser.getAttempts().get(0).getScore());
        assertEquals("R-1", retrievedUser.getAttempts().get(0).getResource().getId());
        
        assertEquals(1, retrievedUser.getProficiencies().size());
        assertEquals(95.0, retrievedUser.getProficiencies().get(0).getPercentage());
        assertEquals("C-1", retrievedUser.getProficiencies().get(0).getConcept().getId());
    }
}
