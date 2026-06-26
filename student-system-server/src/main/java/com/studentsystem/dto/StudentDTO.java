package com.studentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentDTO {
    private Long id;

    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    private LocalDate birthDate;
    private String idCard;
    private String phone;
    private String address;

    @NotNull(message = "入学年份不能为空")
    private Integer enrollmentYear;

    private String politicalStatus;

    @NotNull(message = "班级不能为空")
    private Long classId;

    private Long userId;
    private String username;   // 创建关联用户时的用户名
    private String password;   // 创建关联用户时的密码
}
