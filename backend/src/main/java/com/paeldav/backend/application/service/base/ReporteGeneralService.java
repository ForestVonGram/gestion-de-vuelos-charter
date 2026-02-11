package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.reporte.ReporteCreateDTO;
import com.paeldav.backend.application.dto.reporte.ReporteFiltroDTO;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;

import java.util.List;

/**
 * Interfaz de servicio para reportes operativos generales.
 * Define métodos para crear, consultar y administrar reportes.
 */
public interface ReporteGeneralService {

    /**
     * Genera un nuevo reporte operativo.
     *
     * @param createDTO DTO con parámetros de generación del reporte
     * @param usuarioIdAutenticado ID del usuario autenticado que genera el reporte
     * @return ReporteDTO con el reporte generado
     */
    ReporteDTO generarReporteOperativo(ReporteCreateDTO createDTO, Long usuarioIdAutenticado);

    /**
     * Obtiene un reporte por su ID.
     *
     * @param id ID del reporte
     * @return ReporteDTO con los datos del reporte
     */
    ReporteDTO obtenerReportePorId(Long id);

    /**
     * Obtiene todos los reportes con aplicación de filtros.
     *
     * @param filtro DTO con criterios de filtrado
     * @return Lista de ReporteDTO que coinciden con los filtros
     */
    List<ReporteDTO> listarReportes(ReporteFiltroDTO filtro);

    /**
     * Obtiene todos los reportes sin filtros.
     *
     * @return Lista de todos los reportes
     */
    List<ReporteDTO> obtenerTodosReportes();

    /**
     * Elimina un reporte por su ID.
     *
     * @param id ID del reporte a eliminar
     */
    void eliminarReporte(Long id);

    /**
     * Valida que el rango de fechas sea válido.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return true si el rango es válido, false en caso contrario
     */
    boolean validarRangoFechas(Long fechaInicio, Long fechaFin);
}
