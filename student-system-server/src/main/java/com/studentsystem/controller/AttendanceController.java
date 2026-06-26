package com.studentsystem.controller;

import com.studentsystem.common.Result;
import com.studentsystem.dto.AttendanceDTO;
import com.studentsystem.entity.Attendance;
import com.studentsystem.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> record(@Valid @RequestBody List<AttendanceDTO> dtoList) {
        attendanceService.record(dtoList);
        return Result.success();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<Attendance>> listByStudent(@PathVariable Long studentId) {
        return Result.success(attendanceService.listByStudent(studentId));
    }

    @GetMapping("/offering/{offeringId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Attendance>> listByOffering(@PathVariable Long offeringId) {
        return Result.success(attendanceService.listByOffering(offeringId));
    }
}
