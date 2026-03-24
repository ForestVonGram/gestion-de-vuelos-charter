package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.auth.*;

public interface AuthService {
    AuthResponse login(LoginRequest request, String dispositivo, String direccionIp, String userAgent);
    AuthResponse register(RegisterRequest request, String dispositivo, String direccionIp, String userAgent);
    AuthResponse loginConGoogle(GoogleAuthRequest request, String dispositivo, String direccionIp, String userAgent);
    void logout(String token);
    AuthResponse verificarDosFactores(VerificarCodigoRequest request, String dispositivo, String direccionIp, String userAgent);
    void habilitarDosFactores(ConfiguracionDosFactoresDTO config);
    void deshabilitarDosFactores();
    EstadoDosFactoresDTO obtenerEstadoDosFactores();
}
