package com.wu.daglearn;

import com.wu.daglearn.model.Concept;
import com.wu.daglearn.model.McqResource;
import com.wu.daglearn.model.Resource;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.model.TopicProficiency;
import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.ConceptRepository;
import com.wu.daglearn.repository.ResourceRepository;
import com.wu.daglearn.repository.TopicRepository;
import com.wu.daglearn.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final TopicRepository topicRepository;
    private final ConceptRepository conceptRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public DataLoader(TopicRepository topicRepository, ConceptRepository conceptRepository, 
                      ResourceRepository resourceRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.conceptRepository = conceptRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing test graph in Neo4j...");
        
        // Clear existing test data
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Create Topics
        Topic arrays = new Topic("T-1", "Arrays");
        arrays.setRequiredProficiencyScore(80);
        arrays.setDescription("Basic linear data structure where elements are stored in contiguous memory locations.");

        Topic linkedLists = new Topic("T-2", "Linked Lists");
        linkedLists.setRequiredProficiencyScore(80);
        linkedLists.setDescription("Linear data structure where elements are stored in nodes connected by pointers.");

        Topic trees = new Topic("T-3", "Trees");
        trees.setRequiredProficiencyScore(80);
        trees.setDescription("Hierarchical data structure consisting of nodes connected by edges.");

        Topic graphs = new Topic("T-4", "Graphs");
        graphs.setRequiredProficiencyScore(80);
        graphs.setDescription("Collection of nodes and edges connecting pairs of nodes.");

        // 2. Create Concepts for Linked Lists
        Concept nodeStructure = new Concept("C-1", "Node Structure");
        nodeStructure.setDescription("Understanding the data field and the next pointer.");
        
        Concept traversal = new Concept("C-2", "Traversal");
        traversal.setDescription("The logic of visiting nodes until reaching null.");

        // 3. Create Resources (MCQs) for Concepts
        McqResource mcq1 = new McqResource("R-1", 
            "What happens if you lose the head pointer?", 
            List.of("The entire list becomes unreachable (Memory Leak)", "Only the head is lost", "Nothing happens", "The next pointer is updated"),
            "The entire list becomes unreachable (Memory Leak)");
        
        McqResource mcq2 = new McqResource("R-2", 
            "What is the time complexity to access the i-th element in a Singly Linked List?", 
            List.of("O(1)", "O(log n)", "O(n)", "O(n^2)"),
            "O(n)");

        // 4. Build Relationships
        nodeStructure.getResources().add(mcq1);
        traversal.getResources().add(mcq2);

        linkedLists.getConcepts().addAll(Set.of(nodeStructure, traversal));

        // Build the DAG (Prerequisites)
        linkedLists.getPrerequisites().add(arrays);
        trees.getPrerequisites().addAll(Set.of(arrays, linkedLists));
        graphs.getPrerequisites().addAll(Set.of(trees, arrays));

        // 5. Save to Neo4j
        topicRepository.saveAll(Set.of(arrays, linkedLists, trees, graphs));
        
        // 6. Create test user "user-1"
        User user1 = new User("user-1", "Rachel Wu", "rachel@example.com");
        
        // Let's say the user has mastered "Arrays" already
        TopicProficiency arrayProficiency = new TopicProficiency(arrays, 100.0);
        user1.getTopicProficiencies().add(arrayProficiency);
        
        userRepository.save(user1);

        System.out.println("Test graph and user saved successfully!");
        System.out.println("Structure: Arrays (Mastered), Linked Lists -> Trees -> Graphs");
        System.out.println("Unlocked for user-1 should be: [Linked Lists] (since Arrays is already mastered and Linked Lists is available)");
    }
}
