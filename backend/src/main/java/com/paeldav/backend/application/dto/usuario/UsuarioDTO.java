package com.paeldav.backend.application.dto.usuario;

import com.paeldav.backend.domain.enums.RolUsuario;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Usuario.
 * Excluye información sensible como la contraseña.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}
