package com.paeldav.backend.application.dto.alerta;

import com.paeldav.backend.domain.enums.TipoAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para la creación de una nueva Alerta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaCreateDTO {

    @NotNull(message = "La aeronave es obligatoria")
    private Long aeronaveId;

    @NotNull(message = "El tipo de alerta es obligatorio")
    private TipoAlerta tipo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private Long mantenimientoRelacionadoId;

    private String observaciones;
}
