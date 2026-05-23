package com.wu.daglearn.repository;

import com.wu.daglearn.model.Topic;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends Neo4jRepository<Topic, String> {

    /**
     * Finds topics the user has not yet mastered, but for which they have mastered all prerequisites.
     * Roll-up logic: Mastery is checked against the Topic's requiredProficiencyScore.
     */
    @Query("MATCH (u:User {id: $userId}) " +
           "MATCH (nextTopic:Topic) " +
           "WHERE NOT EXISTS { " +
           "    MATCH (u)-[:MASTERED]->(nextTopic) " +
           "} " +
           "AND ALL(prereq IN [(nextTopic)-[:REQUIRES]->(p:Topic) | p] WHERE EXISTS { " +
           "    MATCH (u)-[:MASTERED]->(prereq) " +
           "}) " +
           "RETURN nextTopic")
    List<Topic> findUnlockedTopicsForUser(String userId);

    @Query("MATCH (t:Topic)-[:CONTAINS]->(c:Concept {id: $conceptId}) RETURN t")
    List<Topic> findTopicsByConceptId(String conceptId);
}
