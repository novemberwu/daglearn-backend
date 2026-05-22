package com.wu.daglearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

@RelationshipProperties
@Data
@NoArgsConstructor
public class ResourceAttempt {

    @Id @GeneratedValue
    private Long id;

    private int score; // 0 or 1
    private Instant attemptDate = Instant.now();

    @TargetNode
    private Resource resource;

    public ResourceAttempt(Resource resource, int score) {
        this.resource = resource;
        this.score = score;
    }
}
