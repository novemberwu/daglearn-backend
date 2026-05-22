package com.wu.daglearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@NoArgsConstructor
public class ConceptProficiency {

    @Id @GeneratedValue
    private Long id;

    private double percentage; // 0.0 to 100.0

    @TargetNode
    private Concept concept;

    public ConceptProficiency(Concept concept, double percentage) {
        this.concept = concept;
        this.percentage = percentage;
    }
}
