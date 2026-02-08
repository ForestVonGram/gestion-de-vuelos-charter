package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.domain.enums.EstadoAeronave;

import java.util.List;

/**
 * Interfaz para la gestión integral de aeronaves.
 * Define operaciones CRUD, validación de capacidades y búsquedas especializadas.
 */
public interface AeronaveService {

    /**
     * Registra una nueva aeronave en el sistema.
     * Valida que la matrícula sea única y los datos sean válidos.
     *
     * @param aeronaveCreateDTO DTO con los datos de la nueva aeronave
     * @return DTO de la aeronave registrada
     * @throws com.paeldav.backend.exception.AeronaveYaExisteException si la matrícula ya existe
     */
    AeronaveDTO registrarAeronave(AeronaveCreateDTO aeronaveCreateDTO);

    /**
     * Obtiene una aeronave por su ID.
     *
     * @param id ID de la aeronave
     * @return DTO de la aeronave
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe
     */
    AeronaveDTO obtenerAeronavePorId(Long id);

    /**
     * Obtiene una aeronave por su matrícula.
     *
     * @param matricula matrícula de la aeronave
     * @return DTO de la aeronave
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe
     */
    AeronaveDTO obtenerAeronavePorMatricula(String matricula);

    /**
     * Obtiene todas las aeronaves del sistema.
     *
     * @return lista de DTOs de aeronaves
     */
    List<AeronaveDTO> obtenerTodasAeronaves();

    /**
     * Obtiene aeronaves filtradas por estado operativo.
     *
     * @param estado estado a filtrar
     * @return lista de DTOs de aeronaves en el estado especificado
     */
    List<AeronaveDTO> obtenerAerronavesPorEstado(EstadoAeronave estado);

    /**
     * Obtiene aeronaves filtradas por modelo.
     *
     * @param modelo modelo de aeronave
     * @return lista de DTOs de aeronaves con el modelo especificado
     */
    List<AeronaveDTO> obtenerAerronavesPorModelo(String modelo);

    /**
     * Obtiene aeronaves que cumplen con una capacidad mínima de pasajeros.
     *
     * @param capacidad capacidad mínima de pasajeros requerida
     * @return lista de DTOs de aeronaves que cumplen con la capacidad
     */
    List<AeronaveDTO> obtenerAerronavesPorCapacidad(Integer capacidad);

    /**
     * Actualiza la información técnica de una aeronave existente.
     * No permite cambiar la matrícula ni el modelo.
     *
     * @param id ID de la aeronave a actualizar
     * @param aeronaveUpdateDTO DTO con los datos a actualizar
     * @return DTO de la aeronave actualizada
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe
     */
    AeronaveDTO actualizarAeronave(Long id, AeronaveUpdateDTO aeronaveUpdateDTO);

    /**
     * Cambia el estado operativo de una aeronave.
     * Valida transiciones permitidas entre estados.
     *
     * @param id ID de la aeronave
     * @param nuevoEstado nuevo estado operativo
     * @return DTO de la aeronave con el estado actualizado
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe
     */
    AeronaveDTO cambiarEstadoAeronave(Long id, EstadoAeronave nuevoEstado);

    /**
     * Elimina una aeronave del sistema (borrado lógico).
     * Solo permite eliminar aeronaves en estado DISPONIBLE.
     *
     * @param id ID de la aeronave a eliminar
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe
     * @throws com.paeldav.backend.exception.AeronaveNoDisponibleException si no está disponible
     */
    void eliminarAeronave(Long id);

    /**
     * Valida que una aeronave tenga capacidad suficiente para pasajeros y tripulación.
     * Lanza excepción si la capacidad es insuficiente.
     *
     * @param aeronaveId ID de la aeronave a validar
     * @param numeroPasajeros número de pasajeros a trasportar
     * @param numeroTripulantes número de tripulantes requeridos
     * @throws com.paeldav.backend.exception.CapacidadInsuficienteException si no hay capacidad
     */
    void validarCapacidadOperativa(Long aeronaveId, Integer numeroPasajeros, Integer numeroTripulantes);

    /**
     * Incrementa las horas de vuelo de una aeronave.
     * Se utiliza después de completar un vuelo.
     *
     * @param aeronaveId ID de la aeronave
     * @param horasVuelo horas de vuelo a agregar
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si no existe
     */
    void incrementarHorasVuelo(Long aeronaveId, Double horasVuelo);
}
