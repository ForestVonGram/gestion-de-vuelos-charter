package com.paeldav.backend.application.dto.repostaje;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Repostaje.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepostajeDTO {
    private Long id;
    private Long aeronaveId;
    private String aeronaveMatricula;
    private Long vueloId;
    private Double cantidadLitros;
    private String tipoCombustible;
    private Double nivelAntes;
    private Double nivelDespues;
    private Double precioPorLitro;
    private Double costoTotal;
    private String proveedor;
    private String numeroFactura;
    private LocalDateTime fechaRepostaje;
    private String ubicacion;
    private Long realizadoPorId;
    private String realizadoPorNombre;
    private Double horasVueloAeronave;
    private String observaciones;
}
