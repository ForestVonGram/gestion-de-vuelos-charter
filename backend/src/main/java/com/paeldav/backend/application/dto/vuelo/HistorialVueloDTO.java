package com.paeldav.backend.application.dto.vuelo;

import com.paeldav.backend.domain.enums.EstadoVuelo;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para el historial de cambios de un vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialVueloDTO {

    private Long id;

    private Long vueloId;

    private EstadoVuelo estadoAnterior;

    private EstadoVuelo estadoNuevo;

    private String tipoAccion;

    private String motivo;

    private LocalDateTime fechaCambio;

    private Long usuarioResponsableId;

    private String usuarioResponsableNombre;
}
