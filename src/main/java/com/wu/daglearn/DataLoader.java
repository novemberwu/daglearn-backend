package com.wu.daglearn;

import com.wu.daglearn.model.Course;
import com.wu.daglearn.model.Topic;
import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final ConceptRepository conceptRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(CourseRepository courseRepository, TopicRepository topicRepository,
                      ConceptRepository conceptRepository, 
                      ResourceRepository resourceRepository, UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.conceptRepository = conceptRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing AP CSA Knowledge Graph in Neo4j...");
        
        courseRepository.deleteAll();
        resourceRepository.deleteAll();
        conceptRepository.deleteAll();
        topicRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Create AP CSA Topics (Units 1-10)
        Topic unit1 = createTopic("U1", "Primitive Types", "Variables, data types, and basic arithmetic expressions.");
        Topic unit2 = createTopic("U2", "Using Objects", "Creating and calling methods of existing classes.");
        Topic unit3 = createTopic("U3", "Boolean Expressions & If Statements", "Logic, comparison operators, and conditional branching.");
        Topic unit4 = createTopic("U4", "Iteration", "While loops and for loops for repetitive tasks.");
        Topic unit5 = createTopic("U5", "Writing Classes", "Designing classes, constructors, and encapsulation.");
        Topic unit6 = createTopic("U6", "Array", "Storing collections of primitive or object data in fixed-size arrays.");
        Topic unit7 = createTopic("U7", "ArrayList", "Using the dynamic ArrayList class for resizable collections.");
        Topic unit8 = createTopic("U8", "2D Array", "Working with nested loops and grid-based data structures.");
        Topic unit9 = createTopic("U9", "Recursion", "Solving problems by breaking them into smaller, self-similar sub-problems.");

        // 2. Build the DAG (Prerequisites)
        unit2.getPrerequisites().add(unit1);
        unit3.getPrerequisites().add(unit1);
        unit4.getPrerequisites().add(unit3);
        unit5.getPrerequisites().add(unit2);
        unit6.getPrerequisites().add(unit4);
        unit7.getPrerequisites().add(unit6);
        unit8.getPrerequisites().add(unit6);
        unit9.getPrerequisites().add(unit4);

        // 3. Save Topics
        topicRepository.saveAll(Set.of(unit1, unit2, unit3, unit4, unit5, unit6, unit7, unit8, unit9));

        // 4. Create and Save Course
        Course apCsa = new Course("AP-CSA", "AP Computer Science A", "Computer Science");
        apCsa.getTopics().addAll(Set.of(unit1, unit2, unit3, unit4, unit5, unit6, unit7, unit8, unit9));
        courseRepository.save(apCsa);
        
        // 5. Create test user "rachel@example.com"
        User rachel = new User("rachel@example.com", "Rachel Wu", "rachel@example.com");
        rachel.setPassword(passwordEncoder.encode("password"));
        userRepository.save(rachel);

        log.info("AP CSA Knowledge Graph saved successfully!");
    }

    private Topic createTopic(String id, String title, String description) {
        Topic topic = new Topic(id, title);
        topic.setDescription(description);
        topic.setRequiredProficiencyScore(80);
        return topic;
    }
}
