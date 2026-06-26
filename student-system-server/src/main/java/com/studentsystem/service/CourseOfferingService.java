package com.studentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studentsystem.dto.CourseOfferingDTO;
import com.studentsystem.entity.CourseOffering;

public interface CourseOfferingService {
    IPage<CourseOfferingDTO> page(Integer pageNum, Integer pageSize, String semester);
    CourseOfferingDTO getById(Long id);
    CourseOffering create(CourseOfferingDTO dto);
    CourseOffering update(Long id, CourseOfferingDTO dto);
    void delete(Long id);
}
