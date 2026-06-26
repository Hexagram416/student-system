package com.studentsystem.controller;

import com.studentsystem.common.Result;
import com.studentsystem.entity.Clazz;
import com.studentsystem.service.ClazzService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClazzService clazzService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<Clazz>> list() {
        return Result.success(clazzService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<Clazz> getById(@PathVariable Long id) {
        return Result.success(clazzService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Clazz> create(@RequestBody Clazz clazz) {
        return Result.success("创建成功", clazzService.create(clazz));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Clazz> update(@PathVariable Long id, @RequestBody Clazz clazz) {
        return Result.success("更新成功", clazzService.update(id, clazz));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        clazzService.delete(id);
        return Result.success();
    }
}
