package com.studentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studentsystem.common.Result;
import com.studentsystem.dto.UserDTO;
import com.studentsystem.entity.SysUser;
import com.studentsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<IPage<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping
    public Result<SysUser> create(@Valid @RequestBody UserDTO dto) {
        return Result.success("创建成功", userService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<SysUser> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        return Result.success("更新成功", userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
