package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad que representa una aeronave de la flota.
 * Contiene información técnica y operativa de cada avión.
 */
@Entity
@Table(name = "aeronaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aeronave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La matrícula es obligatoria")
    @Column(nullable = false, unique = true)
    private String matricula;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(nullable = false)
    private String modelo;

    @Column(name = "fabricante")
    private String fabricante;

    @Positive(message = "La capacidad de pasajeros debe ser positiva")
    @Column(name = "capacidad_pasajeros", nullable = false)
    private Integer capacidadPasajeros;

    @Positive(message = "La capacidad de tripulación debe ser positiva")
    @Column(name = "capacidad_tripulacion", nullable = false)
    private Integer capacidadTripulacion;

    @Column(name = "autonomia_km")
    private Double autonomiaKm;

    @Column(name = "velocidad_crucero_kmh")
    private Double velocidadCruceroKmh;

    @Column(name = "fecha_fabricacion")
    private LocalDate fechaFabricacion;

    @Column(name = "fecha_ultima_revision")
    private LocalDate fechaUltimaRevision;

    @Column(name = "horas_vuelo_totales")
    private Double horasVueloTotales = 0.0;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAeronave estado = EstadoAeronave.DISPONIBLE;

    @Column(name = "especificaciones_tecnicas", columnDefinition = "TEXT")
    private String especificacionesTecnicas;

    @OneToMany(mappedBy = "aeronave", cascade = CascadeType.ALL)
    private List<Mantenimiento> historialMantenimiento;
}
