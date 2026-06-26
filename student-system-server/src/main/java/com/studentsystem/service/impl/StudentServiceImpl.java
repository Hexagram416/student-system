package com.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentsystem.common.BusinessException;
import com.studentsystem.dto.StudentDTO;
import com.studentsystem.entity.Student;
import com.studentsystem.entity.SysUser;
import com.studentsystem.mapper.StudentMapper;
import com.studentsystem.mapper.UserMapper;
import com.studentsystem.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public IPage<Student> page(Integer pageNum, Integer pageSize, String keyword) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Student::getStudentNo, keyword)
                   .or()
                   .like(Student::getName, keyword);
        }
        wrapper.orderByAsc(Student::getStudentNo);
        return studentMapper.selectPage(page, wrapper);
    }

    @Override
    public Student getById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return student;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Student create(StudentDTO dto) {
        // Check duplicate student_no
        Student exist = studentMapper.selectOne(
                new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, dto.getStudentNo())
        );
        if (exist != null) {
            throw new BusinessException("学号已存在");
        }

        // Create user account if username provided
        Long userId = null;
        if (StringUtils.hasText(dto.getUsername())) {
            SysUser existUser = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
            );
            if (existUser != null) {
                throw new BusinessException("用户名已存在");
            }
            SysUser user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(
                    StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "123456"
            ));
            user.setRealName(dto.getName());
            user.setRole("STUDENT");
            user.setPhone(dto.getPhone());
            user.setStatus(1);
            userMapper.insert(user);
            userId = user.getId();
        }

        Student student = new Student();
        student.setStudentNo(dto.getStudentNo());
        student.setName(dto.getName());
        student.setGender(dto.getGender());
        student.setBirthDate(dto.getBirthDate());
        student.setIdCard(dto.getIdCard());
        student.setPhone(dto.getPhone());
        student.setAddress(dto.getAddress());
        student.setEnrollmentYear(dto.getEnrollmentYear());
        student.setPoliticalStatus(dto.getPoliticalStatus());
        student.setClassId(dto.getClassId());
        student.setUserId(userId);

        studentMapper.insert(student);
        return student;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Student update(Long id, StudentDTO dto) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        // Check duplicate student_no if changed
        if (!student.getStudentNo().equals(dto.getStudentNo())) {
            Student exist = studentMapper.selectOne(
                    new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, dto.getStudentNo())
            );
            if (exist != null) {
                throw new BusinessException("学号已存在");
            }
        }

        student.setStudentNo(dto.getStudentNo());
        student.setName(dto.getName());
        student.setGender(dto.getGender());
        student.setBirthDate(dto.getBirthDate());
        student.setIdCard(dto.getIdCard());
        student.setPhone(dto.getPhone());
        student.setAddress(dto.getAddress());
        student.setEnrollmentYear(dto.getEnrollmentYear());
        student.setPoliticalStatus(dto.getPoliticalStatus());
        student.setClassId(dto.getClassId());

        studentMapper.updateById(student);
        return student;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        // Also delete associated user account
        if (student.getUserId() != null) {
            userMapper.deleteById(student.getUserId());
        }
        studentMapper.deleteById(id);
    }
}
