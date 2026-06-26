package com.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studentsystem.common.BusinessException;
import com.studentsystem.dto.ScoreInputDTO;
import com.studentsystem.dto.ScoreStatsDTO;
import com.studentsystem.entity.*;
import com.studentsystem.mapper.*;
import com.studentsystem.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseOfferingMapper offeringMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inputScores(ScoreInputDTO dto) {
        // Validate offering
        CourseOffering offering = offeringMapper.selectById(dto.getOfferingId());
        if (offering == null || !"OPEN".equals(offering.getStatus())) {
            throw new BusinessException("该课程当前不可录入成绩");
        }

        List<Score> scoreList = new ArrayList<>();
        for (ScoreInputDTO.ScoreItem item : dto.getScores()) {
            // Validate scores
            if (item.getRegularScore() != null) {
                BigDecimal rs = item.getRegularScore();
                if (rs.compareTo(BigDecimal.ZERO) < 0 || rs.compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessException("平时成绩必须在0-100之间");
                }
            }
            if (item.getExamScore() != null) {
                BigDecimal es = item.getExamScore();
                if (es.compareTo(BigDecimal.ZERO) < 0 || es.compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessException("考试成绩必须在0-100之间");
                }
            }

            // Find enrollment
            Enrollment enrollment = enrollmentMapper.selectOne(
                    new LambdaQueryWrapper<Enrollment>()
                            .eq(Enrollment::getStudentId, item.getStudentId())
                            .eq(Enrollment::getOfferingId, dto.getOfferingId())
                            .eq(Enrollment::getStatus, "ENROLLED")
            );
            if (enrollment == null) {
                throw new BusinessException("学生ID " + item.getStudentId() + " 未选该课程");
            }

            // Check if score already exists
            Score existScore = scoreMapper.selectOne(
                    new LambdaQueryWrapper<Score>().eq(Score::getEnrollmentId, enrollment.getId())
            );

            Score score = existScore != null ? existScore : new Score();
            score.setEnrollmentId(enrollment.getId());
            score.setRegularScore(item.getRegularScore());
            score.setExamScore(item.getExamScore());

            // Calculate total: regular * 0.4 + exam * 0.6
            BigDecimal regular = item.getRegularScore() != null ? item.getRegularScore() : BigDecimal.ZERO;
            BigDecimal exam = item.getExamScore() != null ? item.getExamScore() : BigDecimal.ZERO;
            BigDecimal total = regular.multiply(new BigDecimal("0.4"))
                    .add(exam.multiply(new BigDecimal("0.6")))
                    .setScale(2, RoundingMode.HALF_UP);
            score.setTotalScore(total);

            // Calculate grade point (simplified: total/100 * 4)
            BigDecimal gradePoint = total.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("4"))
                    .setScale(2, RoundingMode.HALF_UP);
            score.setGradePoint(gradePoint);
            score.setStatus("SUBMITTED");
            score.setUpdateTime(LocalDateTime.now());

            if (existScore == null) {
                score.setSubmitTime(LocalDateTime.now());
            }

            scoreList.add(score);
        }

        // Batch save or update
        for (Score score : scoreList) {
            if (score.getId() != null) {
                scoreMapper.updateById(score);
            } else {
                scoreMapper.insert(score);
            }
        }
    }

    @Override
    public List<Score> listByStudent(Long studentId) {
        // Find all enrollments for this student
        List<Enrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getStudentId, studentId)
                        .eq(Enrollment::getStatus, "ENROLLED")
        );

        List<Score> scores = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Score score = scoreMapper.selectOne(
                    new LambdaQueryWrapper<Score>().eq(Score::getEnrollmentId, enrollment.getId())
            );
            if (score != null) {
                scores.add(score);
            }
        }
        return scores;
    }

    @Override
    public List<Score> listByOffering(Long offeringId) {
        return scoreMapper.selectByOfferingId(offeringId);
    }

    @Override
    public ScoreStatsDTO statsByOffering(Long offeringId) {
        List<Score> scores = scoreMapper.selectByOfferingId(offeringId);

        if (scores.isEmpty()) {
            return new ScoreStatsDTO(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal sum = BigDecimal.ZERO;
        int passCount = 0;
        int excellentCount = 0;
        BigDecimal highest = null;
        BigDecimal lowest = null;

        for (Score score : scores) {
            if (score.getTotalScore() == null) continue;
            BigDecimal total = score.getTotalScore();
            sum = sum.add(total);

            if (total.compareTo(new BigDecimal("60")) >= 0) passCount++;
            if (total.compareTo(new BigDecimal("90")) >= 0) excellentCount++;

            if (highest == null || total.compareTo(highest) > 0) highest = total;
            if (lowest == null || total.compareTo(lowest) < 0) lowest = total;
        }

        int count = scores.size();
        BigDecimal average = sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
        BigDecimal passRate = new BigDecimal(passCount)
                .divide(new BigDecimal(count), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal excellentRate = new BigDecimal(excellentCount)
                .divide(new BigDecimal(count), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        return new ScoreStatsDTO(count, average, passRate, excellentRate,
                highest != null ? highest : BigDecimal.ZERO,
                lowest != null ? lowest : BigDecimal.ZERO);
    }
}
