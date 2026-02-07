package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.LoginRequest;
import com.paeldav.backend.application.dto.auth.RegisterRequest;
import com.paeldav.backend.application.service.base.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String dispositivo = extraerDispositivo(httpRequest);
        String direccionIp = extraerDireccionIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        return ResponseEntity.ok(authService.login(request, dispositivo, direccionIp, userAgent));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        String dispositivo = extraerDispositivo(httpRequest);
        String direccionIp = extraerDireccionIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        return ResponseEntity.ok(authService.register(request, dispositivo, direccionIp, userAgent));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    private String extraerDispositivo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Desconocido";

        if (userAgent.contains("Mobile")) return "Móvil";
        if (userAgent.contains("Tablet")) return "Tablet";
        return "Escritorio";
    }

    private String extraerDireccionIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
