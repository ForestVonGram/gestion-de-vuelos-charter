package com.paeldav.backend.application.dto.personal;

import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de respuesta para Personal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private String numeroEmpleado;
    private CargoPersonal cargo;
    private EstadoPersonal estado;
    private String areaEspecializacion;
    private String certificaciones;
    private LocalDate fechaContratacion;
    private String turno;
    private String observaciones;
}
