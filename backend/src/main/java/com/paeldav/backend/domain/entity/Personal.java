package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad que representa al personal de mantenimiento, repostaje y logística.
 * Gestiona información de empleados encargados del soporte operativo de aeronaves.
 */
@Entity
@Table(name = "personal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El número de empleado es obligatorio")
    @Column(name = "numero_empleado", nullable = false, unique = true)
    private String numeroEmpleado;

    @NotNull(message = "El cargo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoPersonal cargo;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPersonal estado = EstadoPersonal.ACTIVO;

    @Column(name = "area_especializacion")
    private String areaEspecializacion;

    @Column(name = "certificaciones", columnDefinition = "TEXT")
    private String certificaciones;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @Column(name = "turno")
    private String turno;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
