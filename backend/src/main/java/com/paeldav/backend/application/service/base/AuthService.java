package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.LoginRequest;
import com.paeldav.backend.application.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request, String dispositivo, String direccionIp, String userAgent);
    AuthResponse register(RegisterRequest request, String dispositivo, String direccionIp, String userAgent);
    void logout(String token);
}
