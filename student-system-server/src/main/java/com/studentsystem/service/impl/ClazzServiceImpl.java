package com.studentsystem.service.impl;

import com.studentsystem.common.BusinessException;
import com.studentsystem.entity.Clazz;
import com.studentsystem.mapper.ClazzMapper;
import com.studentsystem.service.ClazzService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClazzServiceImpl implements ClazzService {

    private final ClazzMapper clazzMapper;

    @Override
    public List<Clazz> list() {
        return clazzMapper.selectList(null);
    }

    @Override
    public Clazz getById(Long id) {
        Clazz clazz = clazzMapper.selectById(id);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }
        return clazz;
    }

    @Override
    public Clazz create(Clazz clazz) {
        clazzMapper.insert(clazz);
        return clazz;
    }

    @Override
    public Clazz update(Long id, Clazz clazz) {
        Clazz exist = clazzMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("班级不存在");
        }
        clazz.setId(id);
        clazzMapper.updateById(clazz);
        return clazz;
    }

    @Override
    public void delete(Long id) {
        if (clazzMapper.selectById(id) == null) {
            throw new BusinessException("班级不存在");
        }
        clazzMapper.deleteById(id);
    }
}
