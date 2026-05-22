package com.wu.daglearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
@Data
@NoArgsConstructor
public class Topic {

    @Id
    private String id;
    private String title;
    private String description;
    
    // Proficiency threshold required to "master" this topic
    private int requiredProficiencyScore;

    // Directed graph: This topic requires the following topics to be learned first
    @Relationship(type = "REQUIRES", direction = Relationship.Direction.OUTGOING)
    private Set<Topic> prerequisites = new HashSet<>();

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private Set<Concept> concepts = new HashSet<>();
    
    public Topic(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
