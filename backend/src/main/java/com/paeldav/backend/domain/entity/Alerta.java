package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.TipoAlerta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una alerta generada por eventos de mantenimiento.
 * Registra alertas de mantenimiento vencido, próximo o en curso.
 */
@Entity
@Table(name = "alertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La aeronave es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aeronave_id", nullable = false)
    private Aeronave aeronave;

    @NotNull(message = "El tipo de alerta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlerta tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mantenimiento_id")
    private Mantenimiento mantenimientoRelacionado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.activa == null) {
            this.activa = true;
        }
    }
}
