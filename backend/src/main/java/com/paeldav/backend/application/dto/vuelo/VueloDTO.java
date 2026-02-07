package com.paeldav.backend.application.dto.vuelo;

import com.paeldav.backend.domain.enums.EstadoVuelo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para Vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VueloDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long aeronaveId;
    private String aeronaveMatricula;
    private List<Long> tripulacionIds;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalidaProgramada;
    private LocalDateTime fechaLlegadaProgramada;
    private LocalDateTime fechaSalidaReal;
    private LocalDateTime fechaLlegadaReal;
    private Integer numeroPasajeros;
    private EstadoVuelo estado;
    private String proposito;
    private String observaciones;
    private LocalDateTime fechaSolicitud;
    private Double costoEstimado;
}
