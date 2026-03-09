package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO con el historial completo de uso de una aeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialUsoAeronaveDTO {

    /**
     * Datos básicos de la aeronave.
     */
    private AeronaveDTO aeronave; // Datos de la aeronave

    /**
     * Lista de vuelos realizados por la aeronave.
     */
    private List<VueloDTO> vuelos; // Vuelos realizados

    /**
     * Lista de mantenimientos realizados a la aeronave.
     */
    private List<MantenimientoDTO> mantenimientos; // Mantenimientos realizados

    /**
     * Lista de repostajes de la aeronave.
     */
    private List<RepostajeDTO> repostajes; // Repostajes realizados

    // ==================== ESTADÍSTICAS ====================

    /**
     * Total de vuelos realizados.
     */
    private Integer totalVuelos; // Número total de vuelos

    /**
     * Total de vuelos completados exitosamente.
     */
    private Integer vuelosCompletados; // Vuelos completados con éxito

    /**
     * Total de vuelos cancelados.
     */
    private Integer vuelosCancelados; // Vuelos cancelados

    /**
     * Total de horas de vuelo acumuladas.
     */
    private Double totalHorasVuelo; // Horas totales de vuelo

    /**
     * Total de mantenimientos realizados.
     */
    private Integer totalMantenimientos; // Número total de mantenimientos

    /**
     * Total de mantenimientos preventivos.
     */
    private Integer mantenimientosPreventivos; // Mantenimientos preventivos

    /**
     * Total de mantenimientos correctivos.
     */
    private Integer mantenimientosCorrectivos; // Mantenimientos correctivos

    /**
     * Costo total de mantenimientos.
     */
    private Double costoTotalMantenimientos; // Costo total en mantenimientos

    /**
     * Total de repostajes realizados.
     */
    private Integer totalRepostajes; // Número total de repostajes

    /**
     * Total de litros de combustible consumidos.
     */
    private Double totalLitrosCombustible; // Litros totales de combustible

    /**
     * Costo total de combustible.
     */
    private Double costoTotalCombustible; // Costo total en combustible

    /**
     * Fecha de inicio del período consultado (null si es historial completo).
     */
    private LocalDateTime fechaDesde; // Inicio del período

    /**
     * Fecha de fin del período consultado (null si es historial completo).
     */
    private LocalDateTime fechaHasta; // Fin del período

    /**
     * Fecha y hora de generación del reporte.
     */
    private LocalDateTime fechaGeneracion; // Fecha de generación del reporte
}