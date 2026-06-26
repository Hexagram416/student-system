package com.studentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceDTO {
    private Long id;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "开课ID不能为空")
    private Long offeringId;

    @NotNull(message = "考勤日期不能为空")
    private LocalDate attendDate;

    @NotNull(message = "教学周不能为空")
    private Integer weekNumber;

    @NotBlank(message = "考勤状态不能为空")
    private String status;

    private String remark;

    // 列表展示时填充
    private String studentName;
    private String courseName;
}
