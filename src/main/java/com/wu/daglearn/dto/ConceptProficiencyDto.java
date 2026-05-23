package com.wu.daglearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConceptProficiencyDto {
    private String conceptId;
    private String conceptName;
    private double percentage;
}
