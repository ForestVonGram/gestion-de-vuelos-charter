package com.paeldav.backend.application.service.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public interface PasswordService {

    void solicitarRecuperacion(String email);

    void resetearPassword(String token, String nuevaPassword);

    void cambiarPassword(Long usuarioId, String passwordActual, String nuevaPassword);

    boolean validarToken(String token);

    String verificarCodigoYGenerarToken(String email, @NotBlank(message = "El código es obligatorio") @Pattern(regexp = "^\\d{6}$", message = "El código debe tener 6 dígitos") String codigo);
}
