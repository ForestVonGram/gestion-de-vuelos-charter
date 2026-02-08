package com.paeldav.backend.application.dto.pago;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para crear un nuevo pago.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoCreateDTO {

    @NotNull(message = "El ID del vuelo es obligatorio")
    private Long vueloId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private Double monto;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "El email debe ser válido")
    private String emailCliente;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String descripcion;
}
