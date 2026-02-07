package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.EstadoTripulante;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad que representa a un miembro de la tripulación (piloto o auxiliar).
 * Gestiona información de certificaciones, horas de vuelo y disponibilidad.
 */
@Entity
@Table(name = "tripulantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tripulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El número de licencia es obligatorio")
    @Column(name = "numero_licencia", nullable = false, unique = true)
    private String numeroLicencia;

    @Column(name = "tipo_licencia")
    private String tipoLicencia;

    @Column(name = "fecha_expedicion_licencia")
    private LocalDate fechaExpedicionLicencia;

    @Column(name = "fecha_vencimiento_licencia")
    private LocalDate fechaVencimientoLicencia;

    @Column(name = "horas_vuelo_totales")
    private Double horasVueloTotales = 0.0;

    @Column(name = "horas_vuelo_mes")
    private Double horasVueloMes = 0.0;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTripulante estado = EstadoTripulante.DISPONIBLE;

    @Column(name = "es_piloto")
    private Boolean esPiloto = false;

    @Column(name = "certificaciones", columnDefinition = "TEXT")
    private String certificaciones;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
