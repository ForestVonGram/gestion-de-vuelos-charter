package com.paeldav.backend.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para recibir el ID Token generado por Google Identity Services
 * desde el frontend y validarlo en el backend.
 */
@Getter
@Setter
@NoArgsConstructor
public class GoogleAuthRequest {

    /** ID Token devuelto por el botón de Google Sign-In (credential). */
    @NotBlank(message = "El credential de Google es obligatorio")
    private String credential;
}
