package com.paeldav.backend.application.dto.nomina;

import com.paeldav.backend.domain.enums.EstadoNomina;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para actualizar una nómina existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NominaUpdateDTO {

    @DecimalMin(value = "0.0", inclusive = true, message = "Las deducciones deben ser positivas o cero")
    private Double deducciones;

    @DecimalMin(value = "0.0", inclusive = true, message = "Las bonificaciones deben ser positivas o cero")
    private Double bonificaciones;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento de impuesto debe ser positivo o cero")
    private Double descuentoImpuesto;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento de afiliación debe ser positivo o cero")
    private Double descuentoAfiliacion;

    private EstadoNomina estado;

    private String observaciones;
}
