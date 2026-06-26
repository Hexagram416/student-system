package com.studentsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsystem.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    @Select("SELECT s.* FROM score s " +
            "JOIN enrollment e ON s.enrollment_id = e.id " +
            "WHERE e.offering_id = #{offeringId}")
    List<Score> selectByOfferingId(@Param("offeringId") Long offeringId);
}
