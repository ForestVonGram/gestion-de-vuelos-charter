package com.paeldav.backend.application.dto.rol;

import com.paeldav.backend.domain.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO que contiene información del usuario y su rol.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleInfoDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
