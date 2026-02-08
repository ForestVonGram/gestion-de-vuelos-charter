package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa el historial de uso completo de una aeronave.
 * Incluye vuelos realizados, mantenimientos, repostajes y estadísticas.
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
    private AeronaveDTO aeronave;

    /**
     * Lista de vuelos realizados por la aeronave.
     */
    private List<VueloDTO> vuelos;

    /**
     * Lista de mantenimientos realizados a la aeronave.
     */
    private List<MantenimientoDTO> mantenimientos;

    /**
     * Lista de repostajes de la aeronave.
     */
    private List<RepostajeDTO> repostajes;

    // ==================== ESTADÍSTICAS ====================

    /**
     * Total de vuelos realizados.
     */
    private Integer totalVuelos;

    /**
     * Total de vuelos completados exitosamente.
     */
    private Integer vuelosCompletados;

    /**
     * Total de vuelos cancelados.
     */
    private Integer vuelosCancelados;

    /**
     * Total de horas de vuelo acumuladas.
     */
    private Double totalHorasVuelo;

    /**
     * Total de mantenimientos realizados.
     */
    private Integer totalMantenimientos;

    /**
     * Total de mantenimientos preventivos.
     */
    private Integer mantenimientosPreventivos;

    /**
     * Total de mantenimientos correctivos.
     */
    private Integer mantenimientosCorrectivos;

    /**
     * Costo total de mantenimientos.
     */
    private Double costoTotalMantenimientos;

    /**
     * Total de repostajes realizados.
     */
    private Integer totalRepostajes;

    /**
     * Total de litros de combustible consumidos.
     */
    private Double totalLitrosCombustible;

    /**
     * Costo total de combustible.
     */
    private Double costoTotalCombustible;

    /**
     * Fecha de inicio del período consultado (null si es historial completo).
     */
    private LocalDateTime fechaDesde;

    /**
     * Fecha de fin del período consultado (null si es historial completo).
     */
    private LocalDateTime fechaHasta;

    /**
     * Fecha y hora de generación del reporte.
     */
    private LocalDateTime fechaGeneracion;
}
