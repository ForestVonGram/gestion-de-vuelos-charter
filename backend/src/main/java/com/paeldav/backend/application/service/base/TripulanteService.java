package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.tripulante.TripulanteCreateDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteUpdateDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de tripulantes (pilotos y auxiliares).
 */
public interface TripulanteService {

    /**
     * Registra un nuevo tripulante en el sistema.
     *
     * @param tripulanteCreateDTO datos del tripulante a registrar
     * @return el tripulante registrado
     */
    TripulanteDTO registrarTripulante(TripulanteCreateDTO tripulanteCreateDTO);

    /**
     * Obtiene un tripulante por su ID.
     *
     * @param id el ID del tripulante
     * @return el tripulante encontrado
     */
    TripulanteDTO obtenerTripulantePorId(Long id);

    /**
     * Obtiene un tripulante por su número de licencia.
     *
     * @param numeroLicencia el número de licencia
     * @return el tripulante encontrado
     */
    TripulanteDTO obtenerTripulantePorNumeroLicencia(String numeroLicencia);

    /**
     * Obtiene todos los tripulantes registrados en el sistema.
     *
     * @return lista de todos los tripulantes
     */
    List<TripulanteDTO> obtenerTodosTripulantes();

    /**
     * Obtiene todos los pilotos registrados.
     *
     * @return lista de pilotos
     */
    List<TripulanteDTO> obtenerPilotos();

    /**
     * Obtiene todos los auxiliares de vuelo registrados.
     *
     * @return lista de auxiliares
     */
    List<TripulanteDTO> obtenerAuxiliares();

    /**
     * Edita la información de un tripulante existente.
     *
     * @param id el ID del tripulante a editar
     * @param tripulanteUpdateDTO datos a actualizar
     * @return el tripulante actualizado
     */
    TripulanteDTO editarTripulante(Long id, TripulanteUpdateDTO tripulanteUpdateDTO);

    /**
     * Elimina un tripulante del sistema.
     *
     * @param id el ID del tripulante a eliminar
     */
    void eliminarTripulante(Long id);

    /**
     * Valida que un tripulante cumpla con todos los requisitos técnicos y normativos.
     *
     * @param id el ID del tripulante a validar
     */
    void validarTripulante(Long id);

    /**
     * Obtiene tripulantes disponibles para ser asignados a un vuelo.
     *
     * @return lista de tripulantes disponibles
     */
    List<TripulanteDTO> obtenerTripulantesDisponibles();
}
