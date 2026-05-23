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
public class TopicProficiency {

    @Id @GeneratedValue
    private Long id;

    @TargetNode
    private Topic topic;

    public TopicProficiency(Topic topic) {
        this.topic = topic;
    }
}
