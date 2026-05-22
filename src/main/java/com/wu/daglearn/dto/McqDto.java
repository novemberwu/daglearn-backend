package com.wu.daglearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Multiple Choice Questions")
public class McqDto {
    @Schema(description = "Unique identifier for the MCQ", example = "R-1")
    private String id;

    @Schema(description = "The question text", example = "What is the time complexity of searching a linked list?")
    private String content;

    @Schema(description = "List of possible answers")
    private List<String> options;
}
