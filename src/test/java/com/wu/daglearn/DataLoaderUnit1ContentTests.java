package com.wu.daglearn;

import com.wu.daglearn.model.*;
import com.wu.daglearn.repository.ConceptRepository;
import com.wu.daglearn.repository.ResourceRepository;
import com.wu.daglearn.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.database.force-reseed=true"
})
public class DataLoaderUnit1ContentTests {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    @DisplayName("Given force-reseed is true, when running DataLoader, then AP CSA Unit 1 concepts and polymorphic resources are cleanly seeded and fully integrated")
    void givenForceReseedTrue_whenRunningDataLoader_thenUnit1ContentIsCleanlySeeded() {
        // Given & When - Database is fully reseeded on startup with force-reseed=true

        // Then - 1. Assert Topic "U1" exists and contains exactly 6 Concepts
        Topic unit1 = topicRepository.findById("U1").orElse(null);
        assertNotNull(unit1, "Topic U1 (Primitive Types) must exist in Neo4j");
        assertEquals("Primitive Types", unit1.getTitle());

        Set<Concept> concepts = unit1.getConcepts();
        assertEquals(6, concepts.size(), "Unit 1 must contain exactly 6 granular Concepts");

        // 2. Traversal and deep assertions on each of the 6 Concepts and their polymorphic resources
        String[] conceptIds = {"C-U1-VAR", "C-U1-TYPE", "C-U1-INT", "C-U1-DBL", "C-U1-STR", "C-U1-CONST"};
        for (String conceptId : conceptIds) {
            Concept concept = conceptRepository.findById(conceptId).orElse(null);
            assertNotNull(concept, "Concept with ID " + conceptId + " must exist");
            assertNotNull(concept.getName());
            assertNotNull(concept.getDescription());

            Set<Resource> resources = concept.getResources();
            assertEquals(2, resources.size(), "Concept " + conceptId + " must be linked to exactly 2 Resources (1 Document, 1 MCQ)");

            // Classify and assert polymorphic types
            DocumentResource document = null;
            McqResource mcq = null;

            for (Resource r : resources) {
                if (r instanceof DocumentResource) {
                    document = (DocumentResource) r;
                } else if (r instanceof McqResource) {
                    mcq = (McqResource) r;
                }
            }

            // Assert Document Resource
            assertNotNull(document, "Concept " + conceptId + " must contain a DocumentResource");
            assertEquals("DOCUMENT", document.getType());
            assertTrue(document.getContent().length() > 100, "Document content must contain detailed markdown text");

            // Assert MCQ Resource
            assertNotNull(mcq, "Concept " + conceptId + " must contain an McqResource");
            assertEquals("MCQ", mcq.getType());
            assertTrue(mcq.getContent().length() > 20, "MCQ content stem must be populated");
            
            List<String> options = mcq.getOptions();
            assertEquals(4, options.size(), "MCQ for concept " + conceptId + " must have exactly 4 choices");
            
            String correctAnswer = mcq.getCorrectAnswer();
            assertNotNull(correctAnswer, "MCQ correct answer must be specified");
            assertTrue(options.contains(correctAnswer), "Correct answer must match one of the available choices");

            // Assert Explanation
            String explanation = mcq.getExplanation();
            assertNotNull(explanation, "MCQ for concept " + conceptId + " must have a detailed explanation populated");
            assertTrue(explanation.length() > 15, "Explanation text must be substantial");
        }
    }
}
