package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloCreateDTO;
import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para la gestión de registros de horas de vuelo de tripulantes.
 * Permite crear, actualizar y consultar registros de horas voladas por tripulante.
 */
public interface RegistroHorasVueloService {

    /**
     * Crea un nuevo registro de horas de vuelo.
     * Valida que el tripulante esté asignado al vuelo y que las horas sean positivas.
     *
     * @param registroCreateDTO DTO con los datos del registro
     * @return DTO del registro creado
     * @throws com.paeldav.backend.exception.TripulanteNoEncontradoException si el tripulante no existe
     * @throws com.paeldav.backend.exception.VueloNoEncontradoException si el vuelo no existe
     * @throws com.paeldav.backend.exception.AsignacionInvalidaException si el tripulante no está asignado al vuelo
     */
    RegistroHorasVueloDTO crearRegistro(RegistroHorasVueloCreateDTO registroCreateDTO);

    /**
     * Obtiene un registro de horas de vuelo por su ID.
     *
     * @param id ID del registro
     * @return DTO del registro
     * @throws IllegalArgumentException si el registro no existe
     */
    RegistroHorasVueloDTO obtenerRegistroPorId(Long id);

    /**
     * Obtiene todos los registros de un tripulante.
     *
     * @param tripulanteId ID del tripulante
     * @return lista de DTOs de registros
     */
    List<RegistroHorasVueloDTO> obtenerRegistrosPorTripulante(Long tripulanteId);

    /**
     * Obtiene todos los registros de un vuelo.
     *
     * @param vueloId ID del vuelo
     * @return lista de DTOs de registros
     */
    List<RegistroHorasVueloDTO> obtenerRegistrosPorVuelo(Long vueloId);

    /**
     * Obtiene registros pendientes de aprobación.
     *
     * @return lista de DTOs de registros no aprobados
     */
    List<RegistroHorasVueloDTO> obtenerRegistrosPendientes();

    /**
     * Calcula las horas totales voladas por un tripulante.
     *
     * @param tripulanteId ID del tripulante
     * @return total de horas voladas (solo registros aprobados)
     */
    Double calcularHorasTotales(Long tripulanteId);

    /**
     * Calcula las horas voladas por un tripulante en un mes específico.
     *
     * @param tripulanteId ID del tripulante
     * @param fecha una fecha dentro del mes a consultar
     * @return total de horas voladas en el mes (solo registros aprobados)
     */
    Double calcularHorasMensuales(Long tripulanteId, LocalDate fecha);

    /**
     * Aprueba un registro de horas de vuelo.
     * Actualiza el estado a aprobado y actualiza las horas totales del tripulante.
     *
     * @param registroId ID del registro a aprobar
     * @param usuarioAprobador ID del usuario que aprueba
     * @return DTO del registro aprobado
     * @throws IllegalArgumentException si el registro no existe o ya está aprobado
     */
    RegistroHorasVueloDTO aprobarRegistro(Long registroId, Long usuarioAprobador);

    /**
     * Elimina un registro de horas de vuelo.
     * Solo se pueden eliminar registros no aprobados.
     *
     * @param registroId ID del registro a eliminar
     * @throws IllegalArgumentException si el registro no existe o ya está aprobado
     */
    void eliminarRegistro(Long registroId);

    /**
     * Obtiene el total de horas de un tripulante en un vuelo específico.
     *
     * @param tripulanteId ID del tripulante
     * @param vueloId ID del vuelo
     * @return total de horas en ese vuelo (solo registros aprobados)
     */
    Double obtenerHorasEnVuelo(Long tripulanteId, Long vueloId);
}
