package com.studentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreStatsDTO {
    private Integer totalCount;       // 总人数
    private BigDecimal averageScore;  // 平均分
    private BigDecimal passRate;      // 及格率 (>=60)
    private BigDecimal excellentRate; // 优秀率 (>=90)
    private BigDecimal highestScore;  // 最高分
    private BigDecimal lowestScore;   // 最低分
}
