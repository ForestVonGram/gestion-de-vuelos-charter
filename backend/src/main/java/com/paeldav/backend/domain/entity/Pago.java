package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.EstadoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una transacción de pago para un vuelo.
 * Registra todos los pagos realizados a través de MercadoPago.
 */
@Entity
@Table(name = "pagos", indexes = {
        @Index(name = "idx_vuelo_id", columnList = "vuelo_id"),
        @Index(name = "idx_estado_pago", columnList = "estado"),
        @Index(name = "idx_referencia_mercadopago", columnList = "referencia_mercadopago")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El vuelo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @NotNull(message = "El usuario cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    @Column(nullable = false)
    private Double monto;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "referencia_mercadopago", unique = true)
    private String referenciaMercadoPago;

    @Column(name = "numero_preferencia")
    private String numeroPreferencia;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "email_cliente")
    private String emailCliente;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
