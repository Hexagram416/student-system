package com.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentsystem.common.BusinessException;
import com.studentsystem.entity.Course;
import com.studentsystem.mapper.CourseMapper;
import com.studentsystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    @Override
    public IPage<Course> page(Integer pageNum, Integer pageSize, String keyword) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getCourseName, keyword)
                   .or()
                   .like(Course::getCourseNo, keyword);
        }
        wrapper.orderByAsc(Course::getCourseNo);
        return courseMapper.selectPage(page, wrapper);
    }

    @Override
    public Course getById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return course;
    }

    @Override
    public Course create(Course course) {
        courseMapper.insert(course);
        return course;
    }

    @Override
    public Course update(Long id, Course course) {
        Course exist = courseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("课程不存在");
        }
        course.setId(id);
        courseMapper.updateById(course);
        return course;
    }

    @Override
    public void delete(Long id) {
        if (courseMapper.selectById(id) == null) {
            throw new BusinessException("课程不存在");
        }
        courseMapper.deleteById(id);
    }
}
