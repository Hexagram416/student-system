package com.studentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseOfferingDTO {
    private Long id;

    @NotNull(message = "课程不能为空")
    private Long courseId;

    @NotNull(message = "教师不能为空")
    private Long teacherId;

    @NotBlank(message = "学期不能为空")
    private String semester;

    private Integer maxCapacity;
    private String schedule;
    private String classroom;
    private String status;

    // 查询时填充的额外字段
    private String courseName;
    private String teacherName;
    private Integer currentCount;
}
