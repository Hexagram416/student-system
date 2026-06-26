package com.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studentsystem.common.BusinessException;
import com.studentsystem.dto.AttendanceDTO;
import com.studentsystem.entity.Attendance;
import com.studentsystem.mapper.AttendanceMapper;
import com.studentsystem.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void record(List<AttendanceDTO> dtoList) {
        for (AttendanceDTO dto : dtoList) {
            Attendance attendance = new Attendance();
            attendance.setStudentId(dto.getStudentId());
            attendance.setOfferingId(dto.getOfferingId());
            attendance.setAttendDate(dto.getAttendDate());
            attendance.setWeekNumber(dto.getWeekNumber());
            attendance.setStatus(dto.getStatus());
            attendance.setRemark(dto.getRemark());
            attendanceMapper.insert(attendance);
        }
    }

    @Override
    public List<Attendance> listByStudent(Long studentId) {
        return attendanceMapper.selectList(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getStudentId, studentId)
                        .orderByDesc(Attendance::getAttendDate)
        );
    }

    @Override
    public List<Attendance> listByOffering(Long offeringId) {
        return attendanceMapper.selectList(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getOfferingId, offeringId)
                        .orderByDesc(Attendance::getAttendDate)
        );
    }
}
