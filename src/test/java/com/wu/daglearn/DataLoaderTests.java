package com.wu.daglearn;

import com.wu.daglearn.model.Course;
import com.wu.daglearn.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.database.force-reseed=false"
})
public class DataLoaderTests {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DataLoader dataLoader;

    @Test
    @DisplayName("Given force-reseed is false and data exists, when running DataLoader, then existing data is preserved")
    void givenForceReseedFalseAndDataExists_whenRunningLoader_thenDataIsPreserved() throws Exception {
        // Given - Ensure at least one course exists (seeded on startup)
        assertTrue(courseRepository.count() > 0, "Database should be seeded on startup");
        long initialCount = courseRepository.count();

        // Create a custom dummy course to simulate user-added or modified data that must be preserved
        Course testCourse = new Course("CUSTOM-TEST", "Custom Test Course", "Computer Science");
        courseRepository.save(testCourse);
        
        long countWithCustom = courseRepository.count();
        assertEquals(initialCount + 1, countWithCustom);

        // When - Run the DataLoader again with force-reseed set to false
        dataLoader.run();

        // Then - Verify that our custom course was NOT wiped and count remains the same (non-destructive)
        assertEquals(countWithCustom, courseRepository.count(), "Custom data must be preserved when force-reseed is false");
        assertTrue(courseRepository.findById("CUSTOM-TEST").isPresent(), "Custom course should still exist");

        // Cleanup
        courseRepository.delete(testCourse);
    }
}
