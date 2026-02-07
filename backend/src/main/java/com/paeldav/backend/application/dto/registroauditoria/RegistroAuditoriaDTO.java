package com.paeldav.backend.application.dto.registroauditoria;

import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para representar un registro de auditoría de acceso.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAuditoriaDTO {
    private Long id;
    private Long usuarioId;
    private TipoEventoAuditoria tipoEvento;
    private LocalDateTime timestamp;
    private String directorIP;
    private String navegador;
    private Boolean resultado;
    private String detallesError;
}
