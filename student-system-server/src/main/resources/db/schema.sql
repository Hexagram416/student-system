-- =============================================
-- Student Management System - Database Schema
-- Database: student_system
-- =============================================

CREATE DATABASE IF NOT EXISTS student_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_system;

-- =============================================
-- Drop tables in reverse dependency order
-- to avoid foreign key constraint issues
-- =============================================

-- Drop tables that reference other tables first
DROP TABLE IF EXISTS score;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS status_change;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS course_offering;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS `class`;
DROP TABLE IF EXISTS major;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS sys_user;

-- =============================================
-- Create tables in dependency order
-- sys_user created last since others may
-- reference it via user_id (no FK constraint)
-- =============================================

-- 1. 院系表 (Department)
CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    dept_no VARCHAR(20) NOT NULL UNIQUE COMMENT '院系编号',
    dept_name VARCHAR(100) NOT NULL COMMENT '院系名称',
    dept_head VARCHAR(50) DEFAULT NULL COMMENT '院系负责人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系表';

-- 2. 专业表 (Major)
CREATE TABLE major (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    major_no VARCHAR(20) NOT NULL UNIQUE COMMENT '专业编号',
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    dept_id BIGINT NOT NULL COMMENT '所属院系ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (dept_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

-- 3. 班级表 (Class)
CREATE TABLE `class` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    class_no VARCHAR(20) NOT NULL UNIQUE COMMENT '班级编号',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    major_id BIGINT NOT NULL COMMENT '所属专业ID',
    grade INT NOT NULL COMMENT '年级',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (major_id) REFERENCES major(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 4. 教师表 (Teacher)
CREATE TABLE teacher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    teacher_no VARCHAR(20) NOT NULL UNIQUE COMMENT '教师编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender CHAR(1) NOT NULL COMMENT '性别 M/F',
    title VARCHAR(50) DEFAULT NULL COMMENT '职称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    dept_id BIGINT NOT NULL COMMENT '所属院系ID',
    user_id BIGINT DEFAULT NULL COMMENT '关联用户账号ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (dept_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 5. 学生表 (Student)
CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender CHAR(1) NOT NULL COMMENT '性别 M/F',
    birth_date DATE DEFAULT NULL COMMENT '出生日期',
    id_card VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(200) DEFAULT NULL COMMENT '家庭住址',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    political_status VARCHAR(20) DEFAULT NULL COMMENT '政治面貌',
    class_id BIGINT NOT NULL COMMENT '所属班级ID',
    user_id BIGINT DEFAULT NULL COMMENT '关联用户账号ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (class_id) REFERENCES `class`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 6. 课程信息表 (Course)
CREATE TABLE course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    course_no VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credit DOUBLE NOT NULL COMMENT '学分',
    total_hours INT NOT NULL COMMENT '总学时',
    course_type VARCHAR(20) NOT NULL COMMENT '课程性质: 必修/选修/公选',
    dept_id BIGINT NOT NULL COMMENT '开设院系ID',
    description TEXT DEFAULT NULL COMMENT '课程描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (dept_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 7. 开课信息表 (CourseOffering)
CREATE TABLE course_offering (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期 e.g. 2025S1',
    max_capacity INT NOT NULL DEFAULT 60 COMMENT '最大选课人数',
    current_count INT NOT NULL DEFAULT 0 COMMENT '当前选课人数',
    schedule VARCHAR(100) DEFAULT NULL COMMENT '上课时间地点',
    classroom VARCHAR(50) DEFAULT NULL COMMENT '教室',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态 OPEN/CLOSED/ENDED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开课信息表';

-- 8. 选课记录表 (Enrollment)
CREATE TABLE enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    offering_id BIGINT NOT NULL COMMENT '开课ID',
    enroll_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED' COMMENT '状态 ENROLLED/DROPPED',
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (offering_id) REFERENCES course_offering(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- 9. 成绩表 (Score)
CREATE TABLE score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    enrollment_id BIGINT NOT NULL COMMENT '选课记录ID',
    regular_score DECIMAL(5,2) DEFAULT NULL COMMENT '平时成绩 0-100',
    exam_score DECIMAL(5,2) DEFAULT NULL COMMENT '考试成绩 0-100',
    total_score DECIMAL(5,2) DEFAULT NULL COMMENT '总评成绩',
    grade_point DECIMAL(3,2) DEFAULT NULL COMMENT '绩点',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/SUBMITTED/PUBLISHED',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    update_time DATETIME DEFAULT NULL COMMENT '最后修改时间',
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 10. 考勤记录表 (Attendance)
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    offering_id BIGINT NOT NULL COMMENT '开课ID',
    attend_date DATE NOT NULL COMMENT '考勤日期',
    week_number INT NOT NULL COMMENT '第几教学周',
    status VARCHAR(20) NOT NULL COMMENT '出勤/迟到/早退/旷课/请假',
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注说明',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (offering_id) REFERENCES course_offering(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- 11. 学籍异动表 (StatusChange)
CREATE TABLE status_change (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    change_type VARCHAR(50) NOT NULL COMMENT '异动类型',
    reason TEXT DEFAULT NULL COMMENT '原因',
    change_date DATE NOT NULL COMMENT '异动日期',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (student_id) REFERENCES student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学籍异动表';

-- 12. 用户表 (sys_user)
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色 ADMIN/TEACHER/STUDENT',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
