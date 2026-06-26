package com.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score")
public class Score implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long enrollmentId;
    private BigDecimal regularScore;
    private BigDecimal examScore;
    private BigDecimal totalScore;
    private BigDecimal gradePoint;
    private String status;
    private LocalDateTime submitTime;
    private LocalDateTime updateTime;
}
