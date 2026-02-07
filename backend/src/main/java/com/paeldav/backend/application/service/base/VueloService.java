package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.dto.vuelo.VueloUpdateDTO;
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
}
