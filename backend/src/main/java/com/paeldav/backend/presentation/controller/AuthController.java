package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.auth.*;
import com.paeldav.backend.application.service.base.AuthService;
import com.paeldav.backend.application.service.base.DosFactoresService;
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
    private final DosFactoresService dosFactoresService;

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

    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verify2FA(
            @Valid @RequestBody VerificarCodigoRequest request,
            HttpServletRequest httpRequest) {
        String dispositivo = extraerDispositivo(httpRequest);
        String direccionIp = extraerDireccionIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            AuthResponse response = authService.verificarDosFactores(request, dispositivo, direccionIp, userAgent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/enable-2fa")
    public ResponseEntity<Void> habilitarDosFactores(
            @Valid @RequestBody ConfiguracionDosFactoresDTO config,
            HttpServletRequest httpRequest) {
        try {
            authService.habilitarDosFactores(config);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<Void> deshabilitarDosFactores() {
        try {
            authService.deshabilitarDosFactores();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/2fa-status")
    public ResponseEntity<EstadoDosFactoresDTO> obtenerEstadoDosFactores() {
        try {
            EstadoDosFactoresDTO estado = authService.obtenerEstadoDosFactores();
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
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
