package com.paeldav.backend.application.dto.reporte;

import com.paeldav.backend.domain.enums.TipoReporte;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para filtrar reportes en consultas.
 * Permite buscar reportes por tipo, rango de fechas y otros parámetros.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteFiltroDTO {

    private TipoReporte tipo; // Filtrar por tipo de reporte
    private LocalDateTime fechaDesde; // Filtrar desde esta fecha
    private LocalDateTime fechaHasta; // Filtrar hasta esta fecha
    private Long generadoPorId; // Filtrar por quien generó el reporte
    @lombok.Builder.Default
    private Integer pagina = 0; // Para paginación
    @lombok.Builder.Default
    private Integer tamanio = 10; // Tamaño de página
}
