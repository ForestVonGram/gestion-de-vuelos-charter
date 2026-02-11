package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.incidencia.IncidenciaCreateDTO;
import com.paeldav.backend.application.dto.incidencia.IncidenciaDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de incidencias técnicas reportadas durante vuelos.
 * Define las operaciones disponibles para crear, consultar y resolver incidencias.
 */
public interface IncidenciaService {

    /**
     * Reporta una nueva incidencia técnica durante un vuelo.
     *
     * @param incidenciaCreateDTO DTO con los datos de la incidencia a reportar
     * @return IncidenciaDTO con la incidencia creada
     */
    IncidenciaDTO reportarIncidencia(IncidenciaCreateDTO incidenciaCreateDTO);

    /**
     * Obtiene una incidencia por su ID.
     *
     * @param id ID de la incidencia
     * @return IncidenciaDTO con los datos de la incidencia
     */
    IncidenciaDTO obtenerIncidenciaPorId(Long id);

    /**
     * Obtiene todas las incidencias registradas en el sistema.
     *
     * @return Lista de IncidenciaDTO
     */
    List<IncidenciaDTO> obtenerTodasIncidencias();

    /**
     * Obtiene todas las incidencias reportadas en un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return Lista de IncidenciaDTO del vuelo
     */
    List<IncidenciaDTO> obtenerIncidenciasPorVuelo(Long vueloId);

    /**
     * Obtiene todas las incidencias pendientes (no resueltas).
     *
     * @return Lista de IncidenciaDTO pendientes
     */
    List<IncidenciaDTO> obtenerIncidenciasPendientes();

    /**
     * Obtiene todas las incidencias no resueltas del sistema.
     *
     * @return Lista de IncidenciaDTO no resueltas
     */
    List<IncidenciaDTO> obtenerIncidenciasNoResueltas();

    /**
     * Obtiene todas las incidencias de un nivel de gravedad específico.
     *
     * @param gravedad nivel de gravedad (BAJA, MEDIA, ALTA, CRITICA)
     * @return Lista de IncidenciaDTO del nivel especificado
     */
    List<IncidenciaDTO> obtenerIncidenciasPorGravedad(String gravedad);

    /**
     * Obtiene todas las incidencias reportadas en un rango de fechas.
     *
     * @param inicio fecha de inicio del rango
     * @param fin fecha de fin del rango
     * @return Lista de IncidenciaDTO en el rango especificado
     */
    List<IncidenciaDTO> obtenerIncidenciasPorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Marca una incidencia como resuelta y registra las acciones tomadas.
     *
     * @param id ID de la incidencia a resolver
     * @param accionesTomadas descripción de las acciones tomadas para resolver
     * @return IncidenciaDTO con la incidencia actualizada
     */
    IncidenciaDTO resolverIncidencia(Long id, String accionesTomadas);
}
