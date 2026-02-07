package com.paeldav.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que registra las horas de vuelo de cada tripulante por vuelo.
 * Permite un tracking detallado del tiempo trabajado y funciones desempeñadas.
 */
@Entity
@Table(name = "registro_horas_vuelo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroHorasVuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tripulante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tripulante_id", nullable = false)
    private Tripulante tripulante;

    @NotNull(message = "El vuelo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @NotNull(message = "Las horas voladas son obligatorias")
    @Positive(message = "Las horas deben ser positivas")
    @Column(name = "horas_voladas", nullable = false)
    private Double horasVoladas;

    @Column(name = "funcion_desempenada")
    private String funcionDesempenada; // PILOTO_COMANDANTE, COPILOTO, AUXILIAR, etc.

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "hora_despegue")
    private LocalDateTime horaDespegue;

    @Column(name = "hora_aterrizaje")
    private LocalDateTime horaAterrizaje;

    @Column(name = "tipo_vuelo")
    private String tipoVuelo; // DIURNO, NOCTURNO, IFR, VFR

    @Column(name = "condiciones_meteorologicas")
    private String condicionesMeteorologicas;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "aprobado")
    private Boolean aprobado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por_id")
    private Usuario aprobadoPor;

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }
}
