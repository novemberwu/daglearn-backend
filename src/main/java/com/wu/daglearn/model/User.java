package com.wu.daglearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("User")
@Data
@NoArgsConstructor
public class User {

    @Id
    private String id; // e.g., email or unique username
    private String username;
    private String email;

    @Relationship(type = "ATTEMPTED")
    private List<ResourceAttempt> attempts = new ArrayList<>();

    @Relationship(type = "UNDERSTANDS")
    private List<ConceptProficiency> proficiencies = new ArrayList<>();

    @Relationship(type = "MASTERED")
    private List<TopicProficiency> topicProficiencies = new ArrayList<>();

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
