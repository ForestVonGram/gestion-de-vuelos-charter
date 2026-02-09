package com.paeldav.backend.application.service.base;

public interface RecaptchaService {

    /**
     * Valida un token de reCAPTCHA con Google.
     * @param token Token del cliente de reCAPTCHA
     * @return true si el token es válido y cumple los requisitos, false en caso contrario
     */
    boolean validarToken(String token);

    /**
     * Obtiene la puntuación del token (para reCAPTCHA v3).
     * @param token Token del cliente de reCAPTCHA
     * @return Puntuación entre 0.0 y 1.0
     */
    double obtenerPuntuacion(String token);

    /**
     * Valida si reCAPTCHA está habilitado en la configuración.
     * @return true si reCAPTCHA está habilitado
     */
    boolean estaHabilitado();
}
