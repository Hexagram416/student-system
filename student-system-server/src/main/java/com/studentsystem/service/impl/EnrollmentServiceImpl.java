package com.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studentsystem.common.BusinessException;
import com.studentsystem.dto.EnrollmentDTO;
import com.studentsystem.entity.*;
import com.studentsystem.mapper.*;
import com.studentsystem.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseOfferingMapper offeringMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Enrollment enroll(EnrollmentDTO dto) {
        // Validate offering
        CourseOffering offering = offeringMapper.selectById(dto.getOfferingId());
        if (offering == null) {
            throw new BusinessException("开课信息不存在");
        }
        if (!"OPEN".equals(offering.getStatus())) {
            throw new BusinessException("该课程当前不可选");
        }
        if (offering.getCurrentCount() >= offering.getMaxCapacity()) {
            throw new BusinessException("课程容量已满");
        }

        // Check duplicate enrollment
        Enrollment exist = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getStudentId, dto.getStudentId())
                        .eq(Enrollment::getOfferingId, dto.getOfferingId())
                        .eq(Enrollment::getStatus, "ENROLLED")
        );
        if (exist != null) {
            throw new BusinessException("已选过该课程，请勿重复选课");
        }

        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(dto.getStudentId());
        enrollment.setOfferingId(dto.getOfferingId());
        enrollment.setStatus("ENROLLED");
        enrollmentMapper.insert(enrollment);

        // Update current_count
        offering.setCurrentCount(offering.getCurrentCount() + 1);
        offeringMapper.updateById(offering);

        return enrollment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void drop(Long id) {
        Enrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("选课记录不存在");
        }
        if ("DROPPED".equals(enrollment.getStatus())) {
            throw new BusinessException("该课程已退选");
        }

        enrollment.setStatus("DROPPED");
        enrollmentMapper.updateById(enrollment);

        // Decrease current_count
        CourseOffering offering = offeringMapper.selectById(enrollment.getOfferingId());
        if (offering != null && offering.getCurrentCount() > 0) {
            offering.setCurrentCount(offering.getCurrentCount() - 1);
            offeringMapper.updateById(offering);
        }
    }

    @Override
    public List<EnrollmentDTO> listByStudent(Long studentId, String semester) {
        List<Enrollment> enrollments;
        if (semester != null && !semester.isEmpty()) {
            enrollments = enrollmentMapper.selectByStudentIdAndSemester(studentId, semester);
        } else {
            enrollments = enrollmentMapper.selectList(
                    new LambdaQueryWrapper<Enrollment>()
                            .eq(Enrollment::getStudentId, studentId)
                            .eq(Enrollment::getStatus, "ENROLLED")
            );
        }
        return enrollments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentDTO> listByOffering(Long offeringId) {
        List<Enrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getOfferingId, offeringId)
                        .eq(Enrollment::getStatus, "ENROLLED")
        );
        return enrollments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private EnrollmentDTO toDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudentId());
        dto.setOfferingId(enrollment.getOfferingId());
        dto.setEnrollDate(enrollment.getEnrollDate());
        dto.setStatus(enrollment.getStatus());

        // Fill student name
        Student student = studentMapper.selectById(enrollment.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getName());
        }

        // Fill course and offering info
        CourseOffering offering = offeringMapper.selectById(enrollment.getOfferingId());
        if (offering != null) {
            dto.setSemester(offering.getSemester());
            dto.setCurrentCount(offering.getCurrentCount());
            dto.setMaxCapacity(offering.getMaxCapacity());

            Course course = courseMapper.selectById(offering.getCourseId());
            if (course != null) {
                dto.setCourseName(course.getCourseName());
            }

            Teacher teacher = teacherMapper.selectById(offering.getTeacherId());
            if (teacher != null) {
                dto.setTeacherName(teacher.getName());
            }
        }

        return dto;
    }
}
