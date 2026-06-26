package com.studentsystem.service;

import com.studentsystem.entity.Clazz;
import java.util.List;

public interface ClazzService {
    List<Clazz> list();
    Clazz getById(Long id);
    Clazz create(Clazz clazz);
    Clazz update(Long id, Clazz clazz);
    void delete(Long id);
}
