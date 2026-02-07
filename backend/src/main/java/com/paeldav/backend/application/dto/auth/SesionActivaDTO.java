package com.paeldav.backend.application.dto.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionActivaDTO {
    private Long id;
    private String dispositivo;
    private String direccionIp;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private LocalDateTime ultimaActividad;
    private boolean sesionActual;
}
