package com.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("teacher")
public class Teacher implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String teacherNo;
    private String name;
    private String gender;
    private String title;
    private String phone;
    private String email;
    private Long deptId;
    private Long userId;
    private LocalDateTime createdAt;
}
