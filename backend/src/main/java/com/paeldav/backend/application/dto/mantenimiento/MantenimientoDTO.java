package com.paeldav.backend.application.dto.mantenimiento;

import com.paeldav.backend.domain.enums.TipoMantenimiento;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Mantenimiento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MantenimientoDTO {
    private Long id;
    private Long aeronaveId;
    private String aeronaveMatricula;
    private TipoMantenimiento tipo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long responsableId;
    private String responsableNombre;
    private Double costo;
    private Double kilometrajeAeronave;
    private Double horasVueloAeronave;
    private String observaciones;
    private Boolean completado;
}
