package com.paeldav.backend.application.dto.usuario;

import com.paeldav.backend.domain.enums.RolUsuario;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para la creación de un nuevo Usuario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;
}
