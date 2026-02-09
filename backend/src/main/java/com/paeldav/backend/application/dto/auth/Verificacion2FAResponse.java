package com.paeldav.backend.application.dto.auth;

import com.paeldav.backend.domain.enums.MetodoDosFactores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verificacion2FAResponse {

    /**
     * Método de 2FA utilizado (EMAIL o SMS)
     */
    private MetodoDosFactores metodo;

    /**
     * Destino enmascarado (email o teléfono parcialmente oculto)
     */
    private String destino;

    /**
     * Tiempo restante en segundos para que expire el código
     */
    private Integer tiempoExpiracion;

    /**
     * Cantidad de intentos restantes antes de bloquear
     */
    private Integer intentosRestantes;
}
