package com.paeldav.backend.application.dto.nomina;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para crear una nueva nómina.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NominaCreateDTO {

    @NotNull(message = "El ID del personal es obligatorio")
    private Long personalId;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mes;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año debe ser válido")
    private Integer ano;

    @NotNull(message = "El salario base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El salario base debe ser positivo o cero")
    private Double salarioBase;

    @DecimalMin(value = "0.0", inclusive = true, message = "Las deducciones deben ser positivas o cero")
    @Builder.Default
    private Double deducciones = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Las bonificaciones deben ser positivas o cero")
    @Builder.Default
    private Double bonificaciones = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento de impuesto debe ser positivo o cero")
    @Builder.Default
    private Double descuentoImpuesto = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento de afiliación debe ser positivo o cero")
    @Builder.Default
    private Double descuentoAfiliacion = 0.0;

    private String observaciones;
}
