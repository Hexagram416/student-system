package com.studentsystem.service;

import com.studentsystem.dto.EnrollmentDTO;
import com.studentsystem.entity.Enrollment;
import java.util.List;

public interface EnrollmentService {
    Enrollment enroll(EnrollmentDTO dto);
    void drop(Long id);
    List<EnrollmentDTO> listByStudent(Long studentId, String semester);
    List<EnrollmentDTO> listByOffering(Long offeringId);
}
