package com.paeldav.backend.application.dto.personal;

import com.paeldav.backend.domain.enums.CargoPersonal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para la creación de un nuevo Personal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalCreateDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El número de empleado es obligatorio")
    private String numeroEmpleado;

    @NotNull(message = "El cargo es obligatorio")
    private CargoPersonal cargo;

    private String areaEspecializacion;

    private String certificaciones;

    private LocalDate fechaContratacion;

    private String turno;

    private String observaciones;
}
