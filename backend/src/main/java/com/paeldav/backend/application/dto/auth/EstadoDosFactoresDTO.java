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
public class EstadoDosFactoresDTO {

    /**
     * Indica si 2FA está habilitado para el usuario
     */
    private Boolean habilitado;

    /**
     * Método de 2FA configurado (EMAIL o SMS)
     */
    private MetodoDosFactores metodo;

    /**
     * Destino enmascarado (email o teléfono parcialmente oculto)
     */
    private String destino;
}
