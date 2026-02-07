package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.registroactividad.RegistroActividadDTO;
import com.paeldav.backend.domain.enums.TipoActividad;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz para la gestión de registros de actividad.
 */
public interface RegistroActividadService {

    /**
     * Registra una actividad realizada por un usuario.
     *
     * @param usuarioId ID del usuario que realizó la actividad
     * @param tipoActividad tipo de actividad
     * @param descripcion descripción de la actividad
     * @param entidadAfectada entidad afectada por la actividad
     * @param detallesAdicionales detalles adicionales
     * @return DTO del registro creado
     */
    RegistroActividadDTO registrarActividad(Long usuarioId, TipoActividad tipoActividad,
                                           String descripcion, String entidadAfectada,
                                           String detallesAdicionales);

    /**
     * Obtiene todas las actividades de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de DTOs de registros de actividad
     */
    List<RegistroActividadDTO> obtenerActividadesPorUsuario(Long usuarioId);

    /**
     * Obtiene todas las actividades de un tipo específico.
     *
     * @param tipoActividad tipo de actividad
     * @return lista de DTOs de registros de actividad
     */
    List<RegistroActividadDTO> obtenerActividadesPorTipo(TipoActividad tipoActividad);

    /**
     * Obtiene actividades en un rango de fechas.
     *
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return lista de DTOs de registros de actividad
     */
    List<RegistroActividadDTO> obtenerActividadesPorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene actividades de un usuario en un rango de fechas.
     *
     * @param usuarioId ID del usuario
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return lista de DTOs de registros de actividad
     */
    List<RegistroActividadDTO> obtenerActividadesPorUsuarioYFecha(Long usuarioId,
                                                                   LocalDateTime inicio,
                                                                   LocalDateTime fin);

    /**
     * Obtiene todas las actividades registradas.
     *
     * @return lista de DTOs de registros de actividad
     */
    List<RegistroActividadDTO> obtenerTodasLasActividades();
}
