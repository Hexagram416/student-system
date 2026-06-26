package com.studentsystem.controller;

import com.studentsystem.common.Result;
import com.studentsystem.dto.EnrollmentDTO;
import com.studentsystem.entity.Enrollment;
import com.studentsystem.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<Enrollment> enroll(@Valid @RequestBody EnrollmentDTO dto) {
        Enrollment enrollment = enrollmentService.enroll(dto);
        return Result.success("选课成功", enrollment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<Void> drop(@PathVariable Long id) {
        enrollmentService.drop(id);
        return Result.success();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<List<EnrollmentDTO>> myEnrollments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String semester) {
        return Result.success(enrollmentService.listByStudent(studentId, semester));
    }

    @GetMapping("/offering/{offeringId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<EnrollmentDTO>> listByOffering(@PathVariable Long offeringId) {
        return Result.success(enrollmentService.listByOffering(offeringId));
    }
}
