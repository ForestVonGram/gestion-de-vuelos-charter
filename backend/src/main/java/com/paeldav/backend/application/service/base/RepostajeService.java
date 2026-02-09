package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.repostaje.RepostajeCreateDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de repostajes.
 * Define operaciones de CRUD y consultas especializadas para registros de combustible.
 */
public interface RepostajeService {

    /**
     * Registra un nuevo repostaje.
     *
     * @param repostajeCreateDTO DTO con los datos del repostaje
     * @return DTO del repostaje registrado
     */
    RepostajeDTO registrarRepostaje(RepostajeCreateDTO repostajeCreateDTO);

    /**
     * Obtiene un repostaje por su ID.
     *
     * @param id ID del repostaje
     * @return DTO del repostaje
     */
    RepostajeDTO obtenerRepostajePorId(Long id);

    /**
     * Obtiene todos los repostajes del sistema.
     *
     * @return Lista de DTOs de repostajes
     */
    List<RepostajeDTO> obtenerTodosRepostajes();

    /**
     * Obtiene todos los repostajes de una aeronave específica.
     *
     * @param aeronaveId ID de la aeronave
     * @return Lista de DTOs de repostajes
     */
    List<RepostajeDTO> obtenerRepostajePorAeronave(Long aeronaveId);

    /**
     * Obtiene todos los repostajes de un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return Lista de DTOs de repostajes
     */
    List<RepostajeDTO> obtenerRepostajePorVuelo(Long vueloId);

    /**
     * Obtiene los repostajes realizados en un rango de fechas.
     *
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return Lista de DTOs de repostajes
     */
    List<RepostajeDTO> obtenerRepostajePorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene los repostajes realizados por un personal específico.
     *
     * @param personalId ID del personal
     * @return Lista de DTOs de repostajes
     */
    List<RepostajeDTO> obtenerRepostajePorPersonal(Long personalId);

    /**
     * Obtiene los últimos repostajes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return Lista de DTOs de los últimos repostajes
     */
    List<RepostajeDTO> obtenerUltimosRepostajes(Long aeronaveId);

    /**
     * Calcula la cantidad total de combustible repostado en una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return Cantidad total en litros
     */
    Double calcularCombustibleTotalAeronave(Long aeronaveId);

    /**
     * Calcula el costo total de repostajes en un período.
     *
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return Costo total
     */
    Double calcularCostoTotalPeriodo(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene los repostajes de un proveedor específico.
     *
     * @param proveedor nombre del proveedor
     * @return Lista de DTOs de repostajes
     */
    List<RepostajeDTO> obtenerRepostajePorProveedor(String proveedor);

    /**
     * Actualiza un repostaje existente.
     *
     * @param id ID del repostaje a actualizar
     * @param repostajeCreateDTO DTO con los nuevos datos
     * @return DTO del repostaje actualizado
     */
    RepostajeDTO actualizarRepostaje(Long id, RepostajeCreateDTO repostajeCreateDTO);

    /**
     * Elimina un repostaje.
     *
     * @param id ID del repostaje a eliminar
     */
    void eliminarRepostaje(Long id);
}
