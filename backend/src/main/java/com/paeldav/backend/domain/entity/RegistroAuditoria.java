package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que registra eventos de acceso y seguridad en el sistema.
 * Mantiene un historial de intentos de login, logout, accesos denegados y otros eventos de seguridad.
 */
@Entity
@Table(name = "registro_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEventoAuditoria tipoEvento;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(length = 50)
    private String directorIP;

    @Column(length = 500)
    private String navegador;

    @Column(nullable = false)
    private Boolean resultado;

    @Column(length = 500)
    private String detallesError;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
