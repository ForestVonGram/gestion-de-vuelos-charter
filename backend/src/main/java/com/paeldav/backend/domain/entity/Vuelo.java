package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.EstadoVuelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un vuelo chárter.
 * Contiene información de la solicitud, asignaciones y seguimiento del vuelo.
 */
@Entity
@Table(name = "vuelos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario solicitante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aeronave_id")
    private Aeronave aeronave;

    @ManyToMany
    @JoinTable(
            name = "vuelo_tripulacion",
            joinColumns = @JoinColumn(name = "vuelo_id"),
            inverseJoinColumns = @JoinColumn(name = "tripulante_id")
    )
    private List<Tripulante> tripulacion;

    @NotBlank(message = "El origen es obligatorio")
    @Column(nullable = false)
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    @Column(nullable = false)
    private String destino;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Column(name = "fecha_salida_programada", nullable = false)
    private LocalDateTime fechaSalidaProgramada;

    @NotNull(message = "La fecha de llegada es obligatoria")
    @Column(name = "fecha_llegada_programada", nullable = false)
    private LocalDateTime fechaLlegadaProgramada;

    @Column(name = "fecha_salida_real")
    private LocalDateTime fechaSalidaReal;

    @Column(name = "fecha_llegada_real")
    private LocalDateTime fechaLlegadaReal;

    @Positive(message = "El número de pasajeros debe ser positivo")
    @Column(name = "numero_pasajeros")
    private Integer numeroPasajeros;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVuelo estado = EstadoVuelo.SOLICITADO;

    @Column(name = "proposito", columnDefinition = "TEXT")
    private String proposito;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "costo_estimado")
    private Double costoEstimado;

    @OneToMany(mappedBy = "vuelo", cascade = CascadeType.ALL)
    private List<Incidencia> incidencias;

    @OneToMany(mappedBy = "vuelo", cascade = CascadeType.ALL)
    private List<PasajeroVuelo> pasajeros;

    @OneToMany(mappedBy = "vuelo", cascade = CascadeType.ALL)
    private List<RegistroHorasVuelo> registrosHoras;

    @OneToMany(mappedBy = "vuelo")
    private List<Repostaje> repostajes;

    @OneToMany(mappedBy = "vuelo", cascade = CascadeType.ALL)
    private List<Pago> pagos;

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
    }
}
