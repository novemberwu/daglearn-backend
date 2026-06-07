package com.wu.daglearn;

import com.wu.daglearn.model.*;
import com.wu.daglearn.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Value("${app.database.force-reseed:false}")
    private boolean forceReseed;

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
        if (!forceReseed && courseRepository.count() > 0) {
            System.out.println("AP CSA Knowledge Graph already exists in Neo4j. Skipping database seeding to prevent destructive overwrites.");
            return;
        }

        System.out.println("Initializing AP CSA Knowledge Graph in Neo4j...");
        
        if (forceReseed) {
            System.out.println("FORCE_RESEED is enabled. Wiping the database before seeding...");
            courseRepository.deleteAll();
            resourceRepository.deleteAll();
            conceptRepository.deleteAll();
            topicRepository.deleteAll();
            userRepository.deleteAll();
        }

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

        // 6. Seed Unit 1 detailed Concepts and Resources (New CSA Content)
        seedCsaUnit1Content();

        System.out.println("AP CSA Knowledge Graph saved successfully!");
    }

    private Topic createTopic(String id, String title, String description) {
        Topic topic = new Topic(id, title);
        topic.setDescription(description);
        topic.setRequiredProficiencyScore(80);
        return topic;
    }

    private void seedCsaUnit1Content() {
        System.out.println("Seeding AP CSA Unit 1 (Primitive Types) Concepts and Resources...");

        // 1. Retrieve the existing U1 Topic
        Topic unit1 = topicRepository.findById("U1").orElseThrow(() -> 
            new IllegalStateException("Topic U1 not found. Ensure core topics are seeded first."));

        // 2. Define the 6 Concepts and their resources
        List<Concept> conceptsToSave = new ArrayList<>();
        List<Resource> resourcesToSave = new ArrayList<>();

        // Concept A: Variables and Scope
        Concept varScope = new Concept("C-U1-VAR", "Variables and Scope");
        varScope.setDescription("Understanding named containers, declaration/assignment, literals, and compile-time scope.");
        DocumentResource varDoc = new DocumentResource("R-U1-VAR-EXP", readResourceFile("csa/U1_C-U1-VAR_DOCUMENT.md"));
        McqResource varMcq = parseMcqFile("R-U1-VAR-Q1", "csa/U1_C-U1-VAR_MEDIUM.md");
        varScope.getResources().addAll(Set.of(varDoc, varMcq));
        conceptsToSave.add(varScope);
        resourcesToSave.addAll(List.of(varDoc, varMcq));

        // Concept B: Data Types
        Concept dataType = new Concept("C-U1-TYPE", "Java Data Types");
        dataType.setDescription("Strong type safety, primitive types, reference types, and address references.");
        DocumentResource typeDoc = new DocumentResource("R-U1-TYP-EXP", readResourceFile("csa/U1_C-U1-TYPE_DOCUMENT.md"));
        McqResource typeMcq = parseMcqFile("R-U1-TYP-Q1", "csa/U1_C-U1-TYPE_MEDIUM.md");
        dataType.getResources().addAll(Set.of(typeDoc, typeMcq));
        conceptsToSave.add(dataType);
        resourcesToSave.addAll(List.of(typeDoc, typeMcq));

        // Concept C: Integer Arithmetic
        Concept intArithmetic = new Concept("C-U1-INT", "Integer Arithmetic");
        intArithmetic.setDescription("32-bit int bounds, division behavior, operator precedence, modulo, and overflow.");
        DocumentResource intDoc = new DocumentResource("R-U1-INT-EXP", readResourceFile("csa/U1_C-U1-INT_DOCUMENT.md"));
        McqResource intMcq = parseMcqFile("R-U1-INT-Q1", "csa/U1_C-U1-INT_MEDIUM.md");
        intArithmetic.getResources().addAll(Set.of(intDoc, intMcq));
        conceptsToSave.add(intArithmetic);
        resourcesToSave.addAll(List.of(intDoc, intMcq));

        // Concept D: Floating-point Precision
        Concept dblPrecision = new Concept("C-U1-DBL", "Floating-point Precision");
        dblPrecision.setDescription("Double representation, floating-point rounding errors, and epsilon comparisons.");
        DocumentResource dblDoc = new DocumentResource("R-U1-DBL-EXP", readResourceFile("csa/U1_C-U1-DBL_DOCUMENT.md"));
        McqResource dblMcq = parseMcqFile("R-U1-DBL-Q1", "csa/U1_C-U1-DBL_MEDIUM.md");
        dblPrecision.getResources().addAll(Set.of(dblDoc, dblMcq));
        conceptsToSave.add(dblPrecision);
        resourcesToSave.addAll(List.of(dblDoc, dblMcq));

        // Concept E: Strings and Immutability
        Concept strImmutability = new Concept("C-U1-STR", "Strings and Immutability");
        strImmutability.setDescription("String pool, heap vs stack, string equality checks, concatenation, and empty vs null.");
        DocumentResource strDoc = new DocumentResource("R-U1-STR-EXP", readResourceFile("csa/U1_C-U1-STR_DOCUMENT.md"));
        McqResource strMcq = parseMcqFile("R-U1-STR-Q1", "csa/U1_C-U1-STR_MEDIUM.md");
        strImmutability.getResources().addAll(Set.of(strDoc, strMcq));
        conceptsToSave.add(strImmutability);
        resourcesToSave.addAll(List.of(strDoc, strMcq));

        // Concept F: Symbolic Constants
        Concept symbolicConstants = new Concept("C-U1-CONST", "Symbolic Constants");
        symbolicConstants.setDescription("Declaring final constants, readability improvements, and single-source updates.");
        DocumentResource constDoc = new DocumentResource("R-U1-CON-EXP", readResourceFile("csa/U1_C-U1-CONST_DOCUMENT.md"));
        McqResource constMcq = parseMcqFile("R-U1-CON-Q1", "csa/U1_C-U1-CONST_MEDIUM.md");
        symbolicConstants.getResources().addAll(Set.of(constDoc, constMcq));
        conceptsToSave.add(symbolicConstants);
        resourcesToSave.addAll(List.of(constDoc, constMcq));

        // 3. Persist resources and concepts
        resourceRepository.saveAll(resourcesToSave);
        conceptRepository.saveAll(conceptsToSave);

        // 4. Update parent U1 Topic node and save
        unit1.getConcepts().addAll(conceptsToSave);
        topicRepository.save(unit1);

        System.out.println("AP CSA Unit 1 detailed content seeded successfully!");
    }

    private String readResourceFile(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream is = resource.getInputStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource file from classpath: " + path, e);
        }
    }

    private McqResource parseMcqFile(String id, String path) {
        String raw = readResourceFile(path);
        String[] sections = raw.split("---");
        if (sections.length < 2) {
            throw new IllegalStateException("MCQ file at " + path + " is missing answer key delimiter '---'");
        }
        
        String mainSection = sections[0].trim();
        String answerSection = sections[1].trim();

        String[] lines = mainSection.split("\n");
        StringBuilder stemBuilder = new StringBuilder();
        List<String> options = new ArrayList<>();
        
        String optA = "", optB = "", optC = "", optD = "";

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("A)")) {
                optA = trimmed.substring(2).trim();
                options.add(optA);
            } else if (trimmed.startsWith("B)")) {
                optB = trimmed.substring(2).trim();
                options.add(optB);
            } else if (trimmed.startsWith("C)")) {
                optC = trimmed.substring(2).trim();
                options.add(optC);
            } else if (trimmed.startsWith("D)")) {
                optD = trimmed.substring(2).trim();
                options.add(optD);
            } else if (options.isEmpty()) {
                stemBuilder.append(line).append("\n");
            }
        }

        String content = stemBuilder.toString().trim();
        String correctAnswer = "";

        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("Correct Answer:\\s*([A-D])").matcher(answerSection);
        if (matcher.find()) {
            String letter = matcher.group(1);
            switch (letter) {
                case "A": correctAnswer = optA; break;
                case "B": correctAnswer = optB; break;
                case "C": correctAnswer = optC; break;
                case "D": correctAnswer = optD; break;
            }
        } else {
            throw new IllegalStateException("MCQ file at " + path + " is missing valid Correct Answer line.");
        }

        String explanation = "";
        int expIdx = answerSection.indexOf("Explanation:");
        if (expIdx != -1) {
            int distractorIdx = answerSection.indexOf("Distractor Analysis:");
            if (distractorIdx != -1 && distractorIdx > expIdx) {
                explanation = answerSection.substring(expIdx + "Explanation:".length(), distractorIdx).trim();
            } else {
                explanation = answerSection.substring(expIdx + "Explanation:".length()).trim();
            }
        }

        return new McqResource(id, content, options, correctAnswer, explanation);
    }
}
