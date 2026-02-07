package com.paeldav.backend.application.dto.tripulante;

import com.paeldav.backend.domain.enums.EstadoTripulante;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de respuesta para Tripulante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripulanteDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private String numeroLicencia;
    private String tipoLicencia;
    private LocalDate fechaExpedicionLicencia;
    private LocalDate fechaVencimientoLicencia;
    private Double horasVueloTotales;
    private Double horasVueloMes;
    private EstadoTripulante estado;
    private Boolean esPiloto;
    private String certificaciones;
    private String observaciones;
}
