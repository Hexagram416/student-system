package com.studentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studentsystem.dto.UserDTO;
import com.studentsystem.entity.SysUser;

public interface UserService {
    IPage<SysUser> page(Integer pageNum, Integer pageSize, String keyword);
    SysUser getById(Long id);
    SysUser create(UserDTO dto);
    SysUser update(Long id, UserDTO dto);
    void delete(Long id);
}
