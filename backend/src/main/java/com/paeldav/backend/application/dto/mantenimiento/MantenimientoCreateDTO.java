package com.paeldav.backend.application.dto.mantenimiento;

import com.paeldav.backend.domain.enums.TipoMantenimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para la creación de un nuevo Mantenimiento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MantenimientoCreateDTO {

    @NotNull(message = "La aeronave es obligatoria")
    private Long aeronaveId;

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private TipoMantenimiento tipo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private LocalDateTime fechaInicio;

    private Long responsableId;

    private Double costo;

    private Double kilometrajeAeronave;

    private Double horasVueloAeronave;

    private String observaciones;
}
