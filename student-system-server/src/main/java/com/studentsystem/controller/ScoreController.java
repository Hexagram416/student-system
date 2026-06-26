package com.studentsystem.controller;

import com.studentsystem.common.Result;
import com.studentsystem.dto.ScoreInputDTO;
import com.studentsystem.dto.ScoreStatsDTO;
import com.studentsystem.entity.Score;
import com.studentsystem.service.ScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> inputScores(@Valid @RequestBody ScoreInputDTO dto) {
        scoreService.inputScores(dto);
        return Result.success();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<Score>> listByStudent(@PathVariable Long studentId) {
        return Result.success(scoreService.listByStudent(studentId));
    }

    @GetMapping("/offering/{offeringId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Score>> listByOffering(@PathVariable Long offeringId) {
        return Result.success(scoreService.listByOffering(offeringId));
    }

    @GetMapping("/stats/{offeringId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ScoreStatsDTO> stats(@PathVariable Long offeringId) {
        return Result.success(scoreService.statsByOffering(offeringId));
    }
}
