package com.paeldav.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad que representa a un pasajero en un vuelo específico.
 * Permite registrar los datos de cada persona que viaja en el vuelo.
 */
@Entity
@Table(name = "pasajeros_vuelo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasajeroVuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El vuelo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @NotBlank(message = "El nombre del pasajero es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido del pasajero es obligatorio")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Column(name = "documento_identidad", nullable = false)
    private String documentoIdentidad;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "contacto_emergencia")
    private String contactoEmergencia;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    @Column(name = "restricciones_medicas", columnDefinition = "TEXT")
    private String restriccionesMedicas;

    @Column(name = "restricciones_alimentarias", columnDefinition = "TEXT")
    private String restriccionesAlimentarias;

    @Column(name = "equipaje_especial", columnDefinition = "TEXT")
    private String equipajeEspecial;

    @Column(name = "asiento_preferido")
    private String asientoPreferido;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Retorna el nombre completo del pasajero.
     */
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
}
