package com.paeldav.backend.application.dto.nomina;

import com.paeldav.backend.domain.enums.EstadoNomina;
import lombok.*;

/**
 * DTO para filtrar nóminas en búsquedas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NominaFiltroDTO {
    private Long personalId;
    private Integer mes;
    private Integer ano;
    private EstadoNomina estado;
}
