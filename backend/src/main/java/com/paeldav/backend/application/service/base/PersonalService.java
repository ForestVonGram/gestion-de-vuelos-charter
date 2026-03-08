package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.personal.PersonalCreateDTO;
import com.paeldav.backend.application.dto.personal.PersonalDTO;
import com.paeldav.backend.application.dto.personal.PersonalUpdateDTO;
import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de personal (mantenimiento, repostaje, logística).
 */
public interface PersonalService {

    /**
     * Registra nuevo personal en el sistema.
     *
     * @param personalCreateDTO datos del personal a registrar
     * @return el personal registrado
     */
    PersonalDTO registrarPersonal(PersonalCreateDTO personalCreateDTO);

    /**
     * Obtiene un personal por su ID.
     *
     * @param id el ID del personal
     * @return el personal encontrado
     */
    PersonalDTO obtenerPersonalPorId(Long id);

    /**
     * Obtiene un personal por su número de empleado.
     *
     * @param numeroEmpleado el número de empleado
     * @return el personal encontrado
     */
    PersonalDTO obtenerPersonalPorNumeroEmpleado(String numeroEmpleado);

    /**
     * Obtiene todo el personal registrado en el sistema.
     *
     * @return lista de todo el personal
     */
    List<PersonalDTO> obtenerTodoPersonal();

    /**
     * Edita la información de un personal existente.
     *
     * @param id el ID del personal a editar
     * @param personalUpdateDTO datos a actualizar
     * @return el personal actualizado
     */

    PersonalDTO editarPersonal(Long id, PersonalUpdateDTO personalUpdateDTO);

    /**
     * Elimina un personal del sistema.
     *
     * @param id el ID del personal a eliminar
     */
    void eliminarPersonal(Long id);

    /**
     * Desactiva un personal en el sistema.
     *
     * @param id el ID del personal a desactivar
     */
    void desactivarPersonal(Long id);

    /**
     * Activa un personal desactivado.
     *
     * @param id el ID del personal a activar
     */
    void activarPersonal(Long id);

    List<PersonalDTO> filtrarPersonal(String nombre, CargoPersonal cargoPersonal, EstadoPersonal estadoPersonal);
}
