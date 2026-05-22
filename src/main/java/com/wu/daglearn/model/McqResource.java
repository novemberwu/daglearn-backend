package com.wu.daglearn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;

@Node("MCQ")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class McqResource extends Resource {

    private List<String> options = new ArrayList<>();
    private String correctAnswer;

    public McqResource(String id, String content, List<String> options, String correctAnswer) {
        super(id, "MCQ", content);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
}
