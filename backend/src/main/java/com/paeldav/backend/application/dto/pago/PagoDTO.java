package com.paeldav.backend.application.dto.pago;

import com.paeldav.backend.domain.enums.EstadoPago;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Pago.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {
    private Long id;
    private Long vueloId;
    private Long usuarioId;
    private String usuarioNombre;
    private Double monto;
    private EstadoPago estado;
    private String referenciaMercadoPago;
    private String numeroPreferencia;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaCreacion;
    private String metodoPago;
    private String emailCliente;
    private String observaciones;
    private String urlPago;
}
