package com.paeldav.backend.application.dto.personal;

import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para la actualización de Personal existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalUpdateDTO {

    private CargoPersonal cargo;

    private EstadoPersonal estado;

    private String areaEspecializacion;

    private String certificaciones;

    private LocalDate fechaContratacion;

    private String turno;

    private String observaciones;
}
