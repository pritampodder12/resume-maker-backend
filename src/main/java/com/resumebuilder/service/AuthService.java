package com.resumebuilder.service;

import com.resumebuilder.dto.request.LoginRequest;
import com.resumebuilder.dto.request.RefreshTokenRequest;
import com.resumebuilder.dto.request.RegisterRequest;
import com.resumebuilder.dto.response.LoginResponse;
import com.resumebuilder.dto.response.UserResponse;

public interface AuthService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    UserResponse getCurrentUser();
}
