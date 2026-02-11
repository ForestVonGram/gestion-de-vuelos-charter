package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.EstadoNomina;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa la nómina de un miembro del personal.
 * Registra el salario, deducciones y bonificaciones mensuales.
 */
@Entity
@Table(name = "nominas", indexes = {
        @Index(name = "idx_personal_id", columnList = "personal_id"),
        @Index(name = "idx_mes_ano", columnList = "mes,ano"),
        @Index(name = "idx_estado", columnList = "estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El personal es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id", nullable = false)
    private Personal personal;

    @NotNull(message = "El mes es obligatorio")
    @Column(nullable = false)
    private Integer mes;

    @NotNull(message = "El año es obligatorio")
    @Column(nullable = false)
    private Integer ano;

    @NotNull(message = "El salario base es obligatorio")
    @PositiveOrZero(message = "El salario base debe ser positivo o cero")
    @Column(nullable = false)
    private Double salarioBase;

    @PositiveOrZero(message = "Las deducciones deben ser positivas o cero")
    @Column(nullable = false)
    @Builder.Default
    private Double deducciones = 0.0;

    @PositiveOrZero(message = "Las bonificaciones deben ser positivas o cero")
    @Column(nullable = false)
    @Builder.Default
    private Double bonificaciones = 0.0;

    @PositiveOrZero(message = "El total neto debe ser positivo o cero")
    @Column(nullable = false)
    private Double totalNeto;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoNomina estado = EstadoNomina.PENDIENTE;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "descuento_impuesto")
    @Builder.Default
    private Double descuentoImpuesto = 0.0;

    @Column(name = "descuento_afiliacion")
    @Builder.Default
    private Double descuentoAfiliacion = 0.0;

    @PrePersist
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
        calcularTotalNeto();
    }

    @PreUpdate
    protected void onUpdate() {
        calcularTotalNeto();
    }

    /**
     * Calcula el total neto de la nómina.
     * Total Neto = Salario Base + Bonificaciones - Deducciones
     */
    private void calcularTotalNeto() {
        this.totalNeto = this.salarioBase + this.bonificaciones - this.deducciones;
        if (this.totalNeto < 0) {
            this.totalNeto = 0.0;
        }
    }
}
