package com.paeldav.backend.application.dto.registroactividad;

import com.paeldav.backend.domain.enums.TipoActividad;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para representar un registro de actividad.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroActividadDTO {
    private Long id;
    private Long usuarioId;
    private TipoActividad tipoActividad;
    private String descripcion;
    private LocalDateTime timestamp;
    private String entidadAfectada;
    private String detallesAdicionales;
}
