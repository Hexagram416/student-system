package com.studentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studentsystem.dto.StudentDTO;
import com.studentsystem.entity.Student;

public interface StudentService {
    IPage<Student> page(Integer pageNum, Integer pageSize, String keyword);
    Student getById(Long id);
    Student create(StudentDTO dto);
    Student update(Long id, StudentDTO dto);
    void delete(Long id);
}
