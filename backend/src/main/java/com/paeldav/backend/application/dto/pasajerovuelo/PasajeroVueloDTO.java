package com.paeldav.backend.application.dto.pasajerovuelo;

import lombok.*;

/**
 * DTO de respuesta para PasajeroVuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasajeroVueloDTO {
    private Long id;
    private Long vueloId;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
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
