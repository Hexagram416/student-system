package com.studentsystem.service;

import com.studentsystem.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> list();
    Department getById(Long id);
    Department create(Department department);
    Department update(Long id, Department department);
    void delete(Long id);
}
