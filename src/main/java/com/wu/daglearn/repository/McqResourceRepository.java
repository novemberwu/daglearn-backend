package com.wu.daglearn.repository;

import com.wu.daglearn.model.McqResource;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface McqResourceRepository extends Neo4jRepository<McqResource, String> {

    @Query("MATCH (t:Topic {id: $topicId})-[:CONTAINS]->(c:Concept)-[:ASSESSED_BY]->(r:MCQ) RETURN r")
    List<McqResource> findAllByTopicId(String topicId);
}
