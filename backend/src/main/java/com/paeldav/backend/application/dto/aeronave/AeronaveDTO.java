package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO con los datos de una aeronave para responder.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AeronaveDTO {
    private Long id; // Identificador único
    private String matricula; // Matrícula de la aeronave
    private String modelo; // Modelo de la aeronave
    private String fabricante; // Fabricante
    private Integer capacidadPasajeros; // Capacidad de pasajeros
    private Integer capacidadTripulacion; // Capacidad de tripulación
    private Double autonomiaKm; // Autonomía en km
    private Double velocidadCruceroKmh; // Velocidad de crucero
    private LocalDate fechaFabricacion; // Fecha de fabricación
    private LocalDate fechaUltimaRevision; // Última revisión
    private Double horasVueloTotales; // Total de horas de vuelo acumuladas
    private EstadoAeronave estado; // Estado actual
    private String especificacionesTecnicas; // Especificaciones técnicas
    private List<ImagenAeronaveDTO> imagenes; // Imágenes de la aeronave
}