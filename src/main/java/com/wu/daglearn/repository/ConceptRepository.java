package com.wu.daglearn.repository;

import com.wu.daglearn.model.Concept;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConceptRepository extends Neo4jRepository<Concept, String> {

    @Query("MATCH (c:Concept)-[:ASSESSED_BY]->(r:Resource {id: $resourceId}) RETURN c")
    Optional<Concept> findByResourceId(String resourceId);
}
