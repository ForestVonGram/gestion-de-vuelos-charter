package com.paeldav.backend.application.dto.auth;

import com.paeldav.backend.domain.enums.MetodoDosFactores;
import jakarta.validation.constraints.NotNull;
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
public class ConfiguracionDosFactoresDTO {

    /**
     * Indica si se desea habilitar o deshabilitar 2FA
     */
    @NotNull(message = "El campo habilitado es obligatorio")
    private Boolean habilitado;

    /**
     * Método de 2FA a utilizar (EMAIL o SMS)
     */
    private MetodoDosFactores metodo;

    /**
     * Destino para el código (email o teléfono)
     * Si es null, se utilizará el del usuario
     */
    private String destino;
}
