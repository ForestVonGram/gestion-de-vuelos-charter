package com.paeldav.backend.application.dto.alerta;

import com.paeldav.backend.domain.enums.TipoAlerta;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Alerta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaDTO {
    private Long id;
    private Long aeronaveId;
    private String aeronaveMatricula;
    private TipoAlerta tipo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private Boolean activa;
    private Long mantenimientoRelacionadoId;
    private String observaciones;
}
