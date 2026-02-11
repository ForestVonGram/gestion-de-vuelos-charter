package com.paeldav.backend.application.dto.nomina;

import com.paeldav.backend.domain.enums.EstadoNomina;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Nómina.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NominaDTO {
    private Long id;
    private Long personalId;
    private String personalNombre;
    private String personalApellido;
    private Integer mes;
    private Integer ano;
    private Double salarioBase;
    private Double deducciones;
    private Double bonificaciones;
    private Double descuentoImpuesto;
    private Double descuentoAfiliacion;
    private Double totalNeto;
    private EstadoNomina estado;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaGeneracion;
    private String observaciones;
}
