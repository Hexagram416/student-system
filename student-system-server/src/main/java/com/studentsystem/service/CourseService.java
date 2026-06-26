package com.studentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studentsystem.entity.Course;

public interface CourseService {
    IPage<Course> page(Integer pageNum, Integer pageSize, String keyword);
    Course getById(Long id);
    Course create(Course course);
    Course update(Long id, Course course);
    void delete(Long id);
}
