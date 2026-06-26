package com.studentsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private Long id;

    @NotNull(message = "学生不能为空")
    private Long studentId;

    @NotNull(message = "开课不能为空")
    private Long offeringId;

    private String status;
    private LocalDateTime enrollDate;

    // 列表展示时填充
    private String studentName;
    private String courseName;
    private String teacherName;
    private String semester;
    private Integer currentCount;
    private Integer maxCapacity;
}
