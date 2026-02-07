package com.paeldav.backend.application.dto.registrohorasvuelo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para RegistroHorasVuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroHorasVueloDTO {
    private Long id;
    private Long tripulanteId;
    private String tripulanteNombre;
    private Long vueloId;
    private String vueloRuta; // origen - destino
    private Double horasVoladas;
    private String funcionDesempenada;
    private LocalDateTime fechaRegistro;
    private LocalDateTime horaDespegue;
    private LocalDateTime horaAterrizaje;
    private String tipoVuelo;
    private String condicionesMeteorologicas;
    private String observaciones;
    private Boolean aprobado;
    private Long aprobadoPorId;
    private String aprobadoPorNombre;
}
