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
public abstract class Resource {

    @Id
    private String id;
    
    private String type; // e.g., MCQ, VIDEO, DOCUMENT
    private String content;

    public Resource(String id, String type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }
}
