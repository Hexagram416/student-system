package com.studentsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ScoreInputDTO {
    @NotNull(message = "开课ID不能为空")
    private Long offeringId;

    @NotNull(message = "成绩列表不能为空")
    private List<ScoreItem> scores;

    @Data
    public static class ScoreItem {
        @NotNull(message = "学生ID不能为空")
        private Long studentId;

        private BigDecimal regularScore;  // 平时成绩 0-100
        private BigDecimal examScore;     // 考试成绩 0-100
    }
}
