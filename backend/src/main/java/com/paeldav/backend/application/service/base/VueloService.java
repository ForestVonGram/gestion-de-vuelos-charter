package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.vuelo.*;
import com.paeldav.backend.domain.enums.EstadoVuelo;

import java.util.List;

/**
 * Interfaz para la gestión de agendamiento de vuelos.
 * Define las operaciones de CRUD y gestión de estados para vuelos chárter.
 */
public interface VueloService {

    /**
     * Crea un nuevo vuelo con los datos proporcionados.
     * El vuelo comienza en estado SOLICITADO.
     *
     * @param vueloCreateDTO DTO con los datos del nuevo vuelo
     * @return DTO del vuelo creado
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si el usuario no existe
     */
    VueloDTO crearVuelo(VueloCreateDTO vueloCreateDTO);

    /**
     * Obtiene un vuelo por su ID.
     *
     * @param id ID del vuelo
     * @return DTO del vuelo
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     */
    VueloDTO obtenerVueloPorId(Long id);

    /**
     * Obtiene todos los vuelos del sistema.
     *
     * @return lista de DTOs de vuelos
     */
    List<VueloDTO> obtenerTodosVuelos();

    /**
     * Actualiza los datos de un vuelo existente.
     * No permite cambiar el estado directamente (usar cambiarEstadoVuelo para eso).
     *
     * @param id ID del vuelo a actualizar
     * @param vueloUpdateDTO DTO con los datos a actualizar
     * @return DTO del vuelo actualizado
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     */
    VueloDTO actualizarVuelo(Long id, VueloUpdateDTO vueloUpdateDTO);

    /**
     * Cancela un vuelo existente.
     * Valida que el vuelo no esté ya en estado CANCELADO o COMPLETADO.
     *
     * @param id ID del vuelo a cancelar
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     * @throws com.paeldav.backend.exception.VueloEstadoInvalidoException si el vuelo no puede ser cancelado
     */
    void cancelarVuelo(Long id);

    /**
     * Cambia el estado de un vuelo.
     * Valida transiciones de estado permitidas.
     *
     * @param id ID del vuelo
     * @param nuevoEstado nuevo estado para el vuelo
     * @return DTO del vuelo con el estado actualizado
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     * @throws com.paeldav.backend.exception.VueloEstadoInvalidoException si la transición no es permitida
     */
    VueloDTO cambiarEstadoVuelo(Long id, EstadoVuelo nuevoEstado);

    /**
     * Obtiene todos los vuelos en un estado específico.
     *
     * @param estado estado de vuelo a filtrar
     * @return lista de DTOs de vuelos en el estado especificado
     */
    List<VueloDTO> obtenerVuelosPorEstado(EstadoVuelo estado);

    // ==================== APROBACIÓN Y RECHAZO ====================

    /**
     * Aprueba una solicitud de vuelo.
     * Cambia el estado de SOLICITADO a CONFIRMADO.
     *
     * @param vueloId ID del vuelo a aprobar
     * @param dto DTO con datos de aprobación (motivo opcional, costo estimado)
     * @return DTO del vuelo aprobado
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     * @throws com.paeldav.backend.exception.VueloEstadoInvalidoException si el vuelo no está en estado SOLICITADO
     */
    VueloDTO aprobarSolicitud(Long vueloId, SolicitudAprobacionDTO dto);

    /**
     * Rechaza una solicitud de vuelo.
     * Cambia el estado de SOLICITADO a CANCELADO con motivo.
     *
     * @param vueloId ID del vuelo a rechazar
     * @param dto DTO con motivo de rechazo (obligatorio)
     * @return DTO del vuelo rechazado
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     * @throws com.paeldav.backend.exception.VueloEstadoInvalidoException si el vuelo no está en estado SOLICITADO
     */
    VueloDTO rechazarSolicitud(Long vueloId, SolicitudRechazoDTO dto);

    // ==================== ASIGNACIÓN DE RECURSOS ====================

    /**
     * Asigna una aeronave a un vuelo.
     * Valida disponibilidad, capacidad y conflictos de horario.
     *
     * @param vueloId ID del vuelo
     * @param dto DTO con ID de aeronave a asignar
     * @return DTO del vuelo con aeronave asignada
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe la aeronave
     * @throws com.paeldav.backend.exception.AsignacionInvalidaException si la asignación no es válida
     */
    VueloDTO asignarAeronave(Long vueloId, AsignacionAeronaveDTO dto);

    /**
     * Asigna tripulación a un vuelo.
     * Valida disponibilidad, licencias vigentes y que haya al menos un piloto.
     *
     * @param vueloId ID del vuelo
     * @param dto DTO con lista de IDs de tripulantes a asignar
     * @return DTO del vuelo con tripulación asignada
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     * @throws com.paeldav.backend.exception.TripulanteNoEncontradoException si algún tripulante no existe
     * @throws com.paeldav.backend.exception.AsignacionInvalidaException si la asignación no es válida
     */
    VueloDTO asignarTripulacion(Long vueloId, AsignacionTripulacionDTO dto);

    // ==================== HISTORIAL Y CONSULTAS ====================

    /**
     * Obtiene el historial de cambios de un vuelo.
     *
     * @param vueloId ID del vuelo
     * @return lista de registros de historial ordenados por fecha descendente
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si no existe el vuelo
     */
    List<HistorialVueloDTO> obtenerHistorialVuelo(Long vueloId);

    /**
     * Obtiene todos los vuelos de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return lista de DTOs de vuelos del usuario
     */
    List<VueloDTO> obtenerVuelosPorUsuario(Long usuarioId);
}
