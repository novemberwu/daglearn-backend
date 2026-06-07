package com.wu.daglearn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;

@Node("DOCUMENT")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DocumentResource extends Resource {

    public DocumentResource(String id, String content) {
        super(id, "DOCUMENT", content);
    }
}
