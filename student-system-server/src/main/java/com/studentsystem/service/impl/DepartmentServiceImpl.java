package com.studentsystem.service.impl;

import com.studentsystem.common.BusinessException;
import com.studentsystem.entity.Department;
import com.studentsystem.mapper.DepartmentMapper;
import com.studentsystem.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;

    @Override
    public List<Department> list() {
        return departmentMapper.selectList(null);
    }

    @Override
    public Department getById(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("院系不存在");
        }
        return dept;
    }

    @Override
    public Department create(Department department) {
        departmentMapper.insert(department);
        return department;
    }

    @Override
    public Department update(Long id, Department department) {
        Department exist = departmentMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("院系不存在");
        }
        department.setId(id);
        departmentMapper.updateById(department);
        return department;
    }

    @Override
    public void delete(Long id) {
        if (departmentMapper.selectById(id) == null) {
            throw new BusinessException("院系不存在");
        }
        departmentMapper.deleteById(id);
    }
}
