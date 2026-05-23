package com.wu.daglearn.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Course {

    @Id
    private String id;
    private String name;
    private String subject;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties("course")
    private Set<Topic> topics = new HashSet<>();

    public Course(String id, String name, String subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
    }
}
