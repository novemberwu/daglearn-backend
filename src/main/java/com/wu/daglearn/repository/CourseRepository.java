package com.wu.daglearn.repository;

import com.wu.daglearn.model.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends Neo4jRepository<Course, String> {
    
    @Query("MATCH (c:Course)-[r:CONTAINS]->(t:Topic) WHERE c.id = $id RETURN c, collect(r), collect(t)")
    Optional<Course> findByIdWithTopics(String id);
}
