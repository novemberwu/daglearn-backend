package com.wu.daglearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    private boolean isCorrect;
    private String conceptId;
    private double newProficiencyPercentage;
}
