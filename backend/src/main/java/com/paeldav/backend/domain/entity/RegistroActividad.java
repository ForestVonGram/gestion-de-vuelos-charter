package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.TipoActividad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que registra todas las actividades realizadas por los usuarios en el sistema.
 * Se utiliza para auditoría y trazabilidad de cambios.
 */
@Entity
@Table(name = "registro_actividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipoActividad;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(length = 100)
    private String entidadAfectada;

    @Column(length = 1000)
    private String detallesAdicionales;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
