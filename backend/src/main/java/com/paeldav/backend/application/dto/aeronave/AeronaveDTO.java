package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de respuesta para Aeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AeronaveDTO {
    private Long id;
    private String matricula;
    private String modelo;
    private String fabricante;
    private Integer capacidadPasajeros;
    private Integer capacidadTripulacion;
    private Double autonomiaKm;
    private Double velocidadCruceroKmh;
    private LocalDate fechaFabricacion;
    private LocalDate fechaUltimaRevision;
    private Double horasVueloTotales;
    private EstadoAeronave estado;
    private String especificacionesTecnicas;
}
