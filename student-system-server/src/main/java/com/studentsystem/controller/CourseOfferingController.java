package com.studentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studentsystem.common.Result;
import com.studentsystem.dto.CourseOfferingDTO;
import com.studentsystem.entity.CourseOffering;
import com.studentsystem.service.CourseOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-offerings")
@RequiredArgsConstructor
public class CourseOfferingController {

    private final CourseOfferingService offeringService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<IPage<CourseOfferingDTO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String semester) {
        return Result.success(offeringService.page(pageNum, pageSize, semester));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<CourseOfferingDTO> getById(@PathVariable Long id) {
        return Result.success(offeringService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CourseOffering> create(@Valid @RequestBody CourseOfferingDTO dto) {
        return Result.success("创建成功", offeringService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CourseOffering> update(@PathVariable Long id, @Valid @RequestBody CourseOfferingDTO dto) {
        return Result.success("更新成功", offeringService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        offeringService.delete(id);
        return Result.success();
    }
}
