package com.wu.daglearn.controller;

import com.wu.daglearn.dto.AnswerSubmissionDto;
import com.wu.daglearn.dto.AttemptResultDto;
import com.wu.daglearn.service.ProficiencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Endpoints for managing users and their learning progress.")
public class UserController {

    private final ProficiencyService proficiencyService;

    public UserController(ProficiencyService proficiencyService) {
        this.proficiencyService = proficiencyService;
    }

    @PostMapping("/{userId}/attempts")
    @Operation(summary = "Submit an answer", description = "Submits an answer for an MCQ, grades it, and updates the user's proficiency for the related concept.")
    public ResponseEntity<AttemptResultDto> submitAnswer(
            @PathVariable String userId,
            @RequestBody AnswerSubmissionDto submission) {
        AttemptResultDto result = proficiencyService.submitAnswer(userId, submission);
        return ResponseEntity.ok(result);
    }
}
