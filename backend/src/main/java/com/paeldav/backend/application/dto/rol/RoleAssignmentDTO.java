package com.paeldav.backend.application.dto.rol;

import com.paeldav.backend.domain.enums.RolUsuario;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asignar o modificar el rol de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAssignmentDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario nuevoRol;

    private String motivo; // Opcional: razón del cambio de rol
}
