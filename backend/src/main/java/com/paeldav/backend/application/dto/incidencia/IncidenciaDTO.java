package com.paeldav.backend.application.dto.incidencia;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Incidencia.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaDTO {
    private Long id;
    private Long vueloId;
    private String vueloOrigen;
    private String vueloDestino;
    private Long reportadoPorId;
    private String reportadoPorNombre;
    private String titulo;
    private String descripcion;
    private String gravedad;
    private LocalDateTime fechaReporte;
    private LocalDateTime fechaResolucion;
    private Boolean resuelta;
    private String accionesTomadas;
    private Long resueltoPorId;
    private String resueltoPorNombre;
}
