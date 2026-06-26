package com.studentsystem.service;

import com.studentsystem.dto.LoginRequest;
import com.studentsystem.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    Object getCurrentUser();
}
