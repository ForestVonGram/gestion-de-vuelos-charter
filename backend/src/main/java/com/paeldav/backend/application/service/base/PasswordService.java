package com.paeldav.backend.application.service.base;

public interface PasswordService {

    void solicitarRecuperacion(String email);

    void resetearPassword(String token, String nuevaPassword);

    void cambiarPassword(Long usuarioId, String passwordActual, String nuevaPassword);

    boolean validarToken(String token);
}
