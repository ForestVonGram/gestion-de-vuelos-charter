package com.paeldav.backend.application.dto.vuelo;

import com.paeldav.backend.domain.enums.EstadoVuelo;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la actualización de un Vuelo existente.
 * Permite asignar aeronave, tripulación y actualizar estado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VueloUpdateDTO {

    private Long aeronaveId;

    private List<Long> tripulacionIds;

    private String origen;

    private String destino;

    private LocalDateTime fechaSalidaProgramada;

    private LocalDateTime fechaLlegadaProgramada;

    private LocalDateTime fechaSalidaReal;

    private LocalDateTime fechaLlegadaReal;

    @Positive(message = "El número de pasajeros debe ser positivo")
    private Integer numeroPasajeros;

    private EstadoVuelo estado;

    private String proposito;

    private String observaciones;

    private Double costoEstimado;
}
