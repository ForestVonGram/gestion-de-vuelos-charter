package com.paeldav.backend.application.dto.disponibilidad;

import com.paeldav.backend.domain.enums.EstadoVuelo;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO que representa un conflicto de agenda detectado.
 * Contiene información del vuelo que ocupa el recurso en el rango solicitado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConflictoAgendaDTO {

    private Long vueloId;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private EstadoVuelo estadoVuelo;
    private String descripcion;
}
