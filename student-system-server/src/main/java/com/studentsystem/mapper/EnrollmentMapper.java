package com.studentsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsystem.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {

    @Select("SELECT e.* FROM enrollment e " +
            "JOIN course_offering co ON e.offering_id = co.id " +
            "WHERE e.student_id = #{studentId} AND co.semester = #{semester}")
    List<Enrollment> selectByStudentIdAndSemester(@Param("studentId") Long studentId,
                                                   @Param("semester") String semester);
}
