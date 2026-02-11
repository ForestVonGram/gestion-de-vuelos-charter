package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.TipoReporte;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un reporte operativo generado en el sistema.
 * Almacena información sobre reportes de flota, horas trabajadas y operaciones generales.
 */
@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de reporte es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "La fecha de generación es obligatoria")
    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @NotNull(message = "La fecha de inicio del rango es obligatoria")
    @Column(name = "fecha_inicio_rango", nullable = false)
    private LocalDateTime fechaInicioRango;

    @NotNull(message = "La fecha de fin del rango es obligatoria")
    @Column(name = "fecha_fin_rango", nullable = false)
    private LocalDateTime fechaFinRango;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generado_por_id", nullable = false)
    private Usuario generadoPor;

    @Column(name = "ruta_archivo")
    private String rutaArchivo; // Ruta del archivo PDF generado si aplica

    @Column(name = "datos_agregados", columnDefinition = "JSONB") // Almacena datos del reporte en formato JSON
    private String datosAgregados;

    @Column(name = "numero_registros")
    @lombok.Builder.Default
    private Integer numeroRegistros = 0;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
    }
}
