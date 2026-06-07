package com.wu.daglearn.repository;

import com.wu.daglearn.model.DocumentResource;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentResourceRepository extends Neo4jRepository<DocumentResource, String> {

    @Query("MATCH (c:Concept {id: $conceptId})-[:ASSESSED_BY]->(r:DOCUMENT) RETURN r")
    List<DocumentResource> findByConceptId(String conceptId);
}
