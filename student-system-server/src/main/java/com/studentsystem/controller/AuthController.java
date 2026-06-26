package com.studentsystem.controller;

import com.studentsystem.common.Result;
import com.studentsystem.dto.LoginRequest;
import com.studentsystem.dto.LoginResponse;
import com.studentsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    @GetMapping("/me")
    public Result<Object> me() {
        return Result.success(authService.getCurrentUser());
    }
}
