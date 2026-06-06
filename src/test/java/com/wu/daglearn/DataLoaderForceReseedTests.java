package com.wu.daglearn;

import com.wu.daglearn.model.Course;
import com.wu.daglearn.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestPropertySource(properties = {
    "app.database.force-reseed=true"
})
public class DataLoaderForceReseedTests {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DataLoader dataLoader;

    @Test
    @DisplayName("Given force-reseed is true and data exists, when running DataLoader, then existing data is wiped and reseeded")
    void givenForceReseedTrueAndDataExists_whenRunningLoader_thenDataIsWipedAndReseeded() throws Exception {
        // Given - Create a custom dummy course to simulate data in the database
        Course testCourse = new Course("CUSTOM-TEST-RESEED", "Custom Test Course for Reseed", "Computer Science");
        courseRepository.save(testCourse);
        assertTrue(courseRepository.findById("CUSTOM-TEST-RESEED").isPresent());

        // When - Run the DataLoader again with force-reseed set to true
        dataLoader.run();

        // Then - Verify that our custom course WAS wiped (since forceReseed is enabled)
        assertFalse(courseRepository.findById("CUSTOM-TEST-RESEED").isPresent(), "Custom course must be wiped when force-reseed is true");
        assertTrue(courseRepository.findById("AP-CSA").isPresent(), "AP CSA Course should be re-seeded cleanly");
    }
}
