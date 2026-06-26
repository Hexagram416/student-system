package com.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentsystem.common.BusinessException;
import com.studentsystem.dto.CourseOfferingDTO;
import com.studentsystem.entity.Course;
import com.studentsystem.entity.CourseOffering;
import com.studentsystem.entity.Teacher;
import com.studentsystem.mapper.CourseMapper;
import com.studentsystem.mapper.CourseOfferingMapper;
import com.studentsystem.mapper.TeacherMapper;
import com.studentsystem.service.CourseOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseOfferingServiceImpl implements CourseOfferingService {

    private final CourseOfferingMapper offeringMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;

    @Override
    public IPage<CourseOfferingDTO> page(Integer pageNum, Integer pageSize, String semester) {
        Page<CourseOffering> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseOffering> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(semester)) {
            wrapper.eq(CourseOffering::getSemester, semester);
        }
        wrapper.orderByDesc(CourseOffering::getCreatedAt);
        IPage<CourseOffering> result = offeringMapper.selectPage(page, wrapper);

        return result.convert(this::toDTO);
    }

    @Override
    public CourseOfferingDTO getById(Long id) {
        CourseOffering offering = offeringMapper.selectById(id);
        if (offering == null) {
            throw new BusinessException("开课信息不存在");
        }
        return toDTO(offering);
    }

    @Override
    public CourseOffering create(CourseOfferingDTO dto) {
        CourseOffering offering = new CourseOffering();
        offering.setCourseId(dto.getCourseId());
        offering.setTeacherId(dto.getTeacherId());
        offering.setSemester(dto.getSemester());
        offering.setMaxCapacity(dto.getMaxCapacity() != null ? dto.getMaxCapacity() : 60);
        offering.setCurrentCount(0);
        offering.setSchedule(dto.getSchedule());
        offering.setClassroom(dto.getClassroom());
        offering.setStatus("OPEN");
        offeringMapper.insert(offering);
        return offering;
    }

    @Override
    public CourseOffering update(Long id, CourseOfferingDTO dto) {
        CourseOffering offering = offeringMapper.selectById(id);
        if (offering == null) {
            throw new BusinessException("开课信息不存在");
        }
        offering.setCourseId(dto.getCourseId());
        offering.setTeacherId(dto.getTeacherId());
        offering.setSemester(dto.getSemester());
        offering.setMaxCapacity(dto.getMaxCapacity());
        offering.setSchedule(dto.getSchedule());
        offering.setClassroom(dto.getClassroom());
        offering.setStatus(dto.getStatus());
        offeringMapper.updateById(offering);
        return offering;
    }

    @Override
    public void delete(Long id) {
        if (offeringMapper.selectById(id) == null) {
            throw new BusinessException("开课信息不存在");
        }
        offeringMapper.deleteById(id);
    }

    private CourseOfferingDTO toDTO(CourseOffering offering) {
        CourseOfferingDTO dto = new CourseOfferingDTO();
        dto.setId(offering.getId());
        dto.setCourseId(offering.getCourseId());
        dto.setTeacherId(offering.getTeacherId());
        dto.setSemester(offering.getSemester());
        dto.setMaxCapacity(offering.getMaxCapacity());
        dto.setCurrentCount(offering.getCurrentCount());
        dto.setSchedule(offering.getSchedule());
        dto.setClassroom(offering.getClassroom());
        dto.setStatus(offering.getStatus());

        // Fill course name
        Course course = courseMapper.selectById(offering.getCourseId());
        if (course != null) {
            dto.setCourseName(course.getCourseName());
        }

        // Fill teacher name
        Teacher teacher = teacherMapper.selectById(offering.getTeacherId());
        if (teacher != null) {
            dto.setTeacherName(teacher.getName());
        }

        return dto;
    }
}
