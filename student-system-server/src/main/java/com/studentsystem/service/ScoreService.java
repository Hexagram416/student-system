package com.studentsystem.service;

import com.studentsystem.dto.ScoreInputDTO;
import com.studentsystem.dto.ScoreStatsDTO;
import com.studentsystem.entity.Score;
import java.util.List;

public interface ScoreService {
    void inputScores(ScoreInputDTO dto);
    List<Score> listByStudent(Long studentId);
    List<Score> listByOffering(Long offeringId);
    ScoreStatsDTO statsByOffering(Long offeringId);
}
