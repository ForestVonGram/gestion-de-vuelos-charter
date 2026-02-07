package com.paeldav.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que registra los repostajes de combustible de las aeronaves.
 * Separada de mantenimiento para control específico de combustible.
 */
@Entity
@Table(name = "repostajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repostaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La aeronave es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aeronave_id", nullable = false)
    private Aeronave aeronave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id")
    private Vuelo vuelo; // Opcional: repostaje antes/después de un vuelo específico

    @NotNull(message = "La cantidad de combustible es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    @Column(name = "cantidad_litros", nullable = false)
    private Double cantidadLitros;

    @Column(name = "tipo_combustible", nullable = false)
    private String tipoCombustible; // JET-A1, AVGAS, etc.

    @Column(name = "nivel_antes")
    private Double nivelAntes; // Nivel de combustible antes del repostaje

    @Column(name = "nivel_despues")
    private Double nivelDespues; // Nivel de combustible después del repostaje

    @Column(name = "precio_por_litro")
    private Double precioPorLitro;

    @Column(name = "costo_total")
    private Double costoTotal;

    @Column(name = "proveedor")
    private String proveedor;

    @Column(name = "numero_factura")
    private String numeroFactura;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha_repostaje", nullable = false)
    private LocalDateTime fechaRepostaje;

    @Column(name = "ubicacion")
    private String ubicacion; // Aeropuerto o ubicación del repostaje

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realizado_por_id")
    private Personal realizadoPor;

    @Column(name = "horas_vuelo_aeronave")
    private Double horasVueloAeronave; // Horas de vuelo al momento del repostaje

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (this.fechaRepostaje == null) {
            this.fechaRepostaje = LocalDateTime.now();
        }
        if (this.costoTotal == null && this.cantidadLitros != null && this.precioPorLitro != null) {
            this.costoTotal = this.cantidadLitros * this.precioPorLitro;
        }
    }
}
