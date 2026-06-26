package com.studentsystem.service;

import com.studentsystem.dto.AttendanceDTO;
import com.studentsystem.entity.Attendance;
import java.util.List;

public interface AttendanceService {
    void record(List<AttendanceDTO> dtoList);
    List<Attendance> listByStudent(Long studentId);
    List<Attendance> listByOffering(Long offeringId);
}
