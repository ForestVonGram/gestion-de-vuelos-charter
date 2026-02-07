package com.paeldav.backend.application.dto.pasajerovuelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para la creación de un PasajeroVuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasajeroVueloCreateDTO {

    @NotNull(message = "El vuelo es obligatorio")
    private Long vueloId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentoIdentidad;

    private String tipoDocumento;
    private String nacionalidad;
    private String telefono;
    private String email;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private String restriccionesMedicas;
    private String restriccionesAlimentarias;
    private String equipajeEspecial;
    private String asientoPreferido;
    private String observaciones;
}
