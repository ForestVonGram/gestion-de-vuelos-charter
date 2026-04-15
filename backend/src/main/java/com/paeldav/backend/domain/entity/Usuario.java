package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.RolUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa a los usuarios del sistema.
 * Incluye todos los roles: Usuario, Administrador, Operador de Logística,
 * Ayudante de Mantenimiento y Tripulación.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String apellido;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(length = 20)
    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "dos_factores_habilitado")
    @Builder.Default
    private Boolean dosFactoresHabilitado = false;

    @Column(name = "metodo_dos_factores")
    @Enumerated(EnumType.STRING)
    private com.paeldav.backend.domain.enums.MetodoDosFactores metodoDosFactores;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name="vuelo_actual")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PasajeroVuelo> vuelos;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}
