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
public class Concept {

    @Id
    private String id;
    private String name;
    private String description;

    @Relationship(type = "ASSESSED_BY", direction = Relationship.Direction.OUTGOING)
    private Set<Resource> resources = new HashSet<>();

    public Concept(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
