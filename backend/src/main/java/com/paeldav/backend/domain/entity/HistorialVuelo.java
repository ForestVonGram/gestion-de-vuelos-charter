package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.EstadoVuelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que registra el historial de cambios de estado de un vuelo.
 * Permite auditar todas las transiciones y acciones realizadas sobre un vuelo.
 */
@Entity
@Table(name = "historial_vuelos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialVuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El vuelo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoVuelo estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo")
    private EstadoVuelo estadoNuevo;

    @Column(name = "tipo_accion", nullable = false)
    private String tipoAccion;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @NotNull(message = "La fecha de cambio es obligatoria")
    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsable_id")
    private Usuario usuarioResponsable;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCambio == null) {
            this.fechaCambio = LocalDateTime.now();
        }
    }
}
