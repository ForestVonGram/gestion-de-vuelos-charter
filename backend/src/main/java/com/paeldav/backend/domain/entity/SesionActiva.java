package com.paeldav.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una sesión activa de usuario.
 * Permite rastrear y gestionar múltiples sesiones por usuario.
 */
@Entity
@Table(name = "sesiones_activas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "dispositivo")
    private String dispositivo;

    @Column(name = "direccion_ip")
    private String direccionIp;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;

    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.ultimaActividad = LocalDateTime.now();
    }

    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }
}
