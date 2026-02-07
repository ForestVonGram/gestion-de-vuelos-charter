package com.paeldav.backend.application.dto.usuario;

import com.paeldav.backend.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para la actualización de un Usuario existente.
 * Todos los campos son opcionales.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateDTO {

    private String nombre;

    private String apellido;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String telefono;

    private RolUsuario rol;

    private Boolean activo;
}
