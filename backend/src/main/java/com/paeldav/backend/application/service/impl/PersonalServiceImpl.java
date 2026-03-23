package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.personal.PersonalCreateDTO;
import com.paeldav.backend.application.dto.personal.PersonalDTO;
import com.paeldav.backend.application.dto.personal.PersonalUpdateDTO;
import com.paeldav.backend.application.mapper.PersonalMapper;
import com.paeldav.backend.application.service.base.PersonalService;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import com.paeldav.backend.exception.PersonalNoEncontradoException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.PersonalRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio {@link PersonalService} encargada de gestionar
 * las operaciones relacionadas con la entidad {@link Personal}.
 *
 * <p>
 * Esta clase contiene la lógica de negocio para:
 * </p>
 * <ul>
 *     <li>Registrar nuevo personal</li>
 *     <li>Consultar personal por ID o número de empleado</li>
 *     <li>Listar todo el personal</li>
 *     <li>Actualizar información del personal</li>
 *     <li>Eliminar personal</li>
 *     <li>Activar o desactivar personal</li>
 *     <li>Filtrar personal por criterios específicos</li>
 * </ul>
 *
 * <p>
 * Utiliza {@link PersonalRepository} para la persistencia,
 * {@link UsuarioRepository} para validar la existencia del usuario asociado
 * y {@link PersonalMapper} para convertir entre entidades y DTOs.
 * </p>
 *
 * <p>
 * Todas las operaciones están manejadas dentro de transacciones mediante
 * {@link Transactional} para garantizar la consistencia de los datos.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PersonalServiceImpl implements PersonalService {

    /**
     * Repositorio para la gestión de la entidad Personal.
     */
    private final PersonalRepository personalRepository;

    /**
     * Repositorio para la gestión de la entidad Usuario.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Mapper encargado de convertir entre entidades {@link Personal}
     * y sus respectivos DTOs.
     */
    private final PersonalMapper personalMapper;

    /**
     * Registra un nuevo miembro del personal en el sistema.
     *
     * <p>
     * Este método realiza las siguientes validaciones:
     * </p>
     * <ul>
     *     <li>Verifica que el usuario asociado exista</li>
     *     <li>Verifica que el número de empleado sea único</li>
     * </ul>
     *
     * Luego convierte el DTO recibido en una entidad,
     * la persiste en la base de datos y devuelve el resultado como DTO.
     *
     * @param personalCreateDTO datos necesarios para crear el personal
     * @return DTO con la información del personal registrado
     * @throws UsuarioNoEncontradoException si el usuario asociado no existe
     * @throws IllegalArgumentException si ya existe un personal con el mismo número de empleado
     */
    @Override
    @Transactional
    public PersonalDTO registrarPersonal(PersonalCreateDTO personalCreateDTO) {

        // Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(personalCreateDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + personalCreateDTO.getUsuarioId()
                ));

        // Verificar que el número de empleado sea único
        if (personalRepository.existsByNumeroEmpleado(personalCreateDTO.getNumeroEmpleado())) {
            throw new IllegalArgumentException(
                    "Ya existe personal con el número de empleado: " + personalCreateDTO.getNumeroEmpleado()
            );
        }

        // Convertir DTO a entidad
        Personal personal = personalMapper.toEntity(personalCreateDTO);
        personal.setUsuario(usuario);

        // Guardar en base de datos
        personal = personalRepository.save(personal);

        return personalMapper.toDTO(personal);
    }

    /**
     * Obtiene la información de un miembro del personal a partir de su ID.
     *
     * @param id identificador único del personal
     * @return DTO con la información del personal
     * @throws PersonalNoEncontradoException si no existe personal con el ID proporcionado
     */
    @Override
    @Transactional(readOnly = true)
    public PersonalDTO obtenerPersonalPorId(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));
        return personalMapper.toDTO(personal);
    }

    /**
     * Obtiene la información de un miembro del personal a partir
     * de su número de empleado.
     *
     * @param numeroEmpleado número único que identifica al empleado
     * @return DTO con la información del personal
     * @throws PersonalNoEncontradoException si no existe personal con ese número
     */
    @Override
    @Transactional(readOnly = true)
    public PersonalDTO obtenerPersonalPorNumeroEmpleado(String numeroEmpleado) {
        Personal personal = personalRepository.findByNumeroEmpleado(numeroEmpleado)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con número de empleado: " + numeroEmpleado
                ));
        return personalMapper.toDTO(personal);
    }

    /**
     * Obtiene una lista con todos los registros de personal.
     *
     * @return lista de {@link PersonalDTO} con todos los miembros del personal
     */
    @Override
    @Transactional(readOnly = true)
    public List<PersonalDTO> obtenerTodoPersonal() {
        List<Personal> personal = personalRepository.findAll();
        return personalMapper.toDTOList(personal);
    }

    /**
     * Actualiza la información de un miembro del personal existente.
     *
     * @param id identificador del personal a actualizar
     * @param personalUpdateDTO datos actualizados del personal
     * @return DTO con la información actualizada
     * @throws PersonalNoEncontradoException si el personal no existe
     */
    @Override
    @Transactional
    public PersonalDTO editarPersonal(Long id, PersonalUpdateDTO personalUpdateDTO) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));

        // Actualizar campos
        personalMapper.updateEntityFromUpdateDTO(personalUpdateDTO, personal);

        personal = personalRepository.save(personal);
        return personalMapper.toDTO(personal);
    }

    /**
     * Elimina permanentemente un registro de personal del sistema.
     *
     * @param id identificador del personal a eliminar
     * @throws PersonalNoEncontradoException si el personal no existe
     */
    @Override
    @Transactional
    public void eliminarPersonal(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));
        personalRepository.delete(personal);
    }

    /**
     * Cambia el estado de un miembro del personal a {@link EstadoPersonal#INACTIVO}.
     *
     * @param id identificador del personal
     * @throws PersonalNoEncontradoException si el personal no existe
     */
    @Override
    @Transactional
    public void desactivarPersonal(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));
        personal.setEstado(EstadoPersonal.INACTIVO);
        personalRepository.save(personal);
    }

    /**
     * Cambia el estado de un miembro del personal a {@link EstadoPersonal#ACTIVO}.
     *
     * @param id identificador del personal
     * @throws PersonalNoEncontradoException si el personal no existe
     */
    @Override
    @Transactional
    public void activarPersonal(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));
        personal.setEstado(EstadoPersonal.ACTIVO);
        personalRepository.save(personal);
    }

    /**
     * Filtra el personal según criterios opcionales de búsqueda.
     *
     * <p>
     * Los parámetros pueden ser nulos. En ese caso no se aplicará
     * el filtro correspondiente.
     * </p>
     *
     * @param nombre nombre del personal a buscar
     * @param cargoPersonal cargo que ocupa el personal
     * @param estadoPersonal estado actual del personal
     * @return lista de personal que cumple con los filtros
     */
    @Override
    public List<PersonalDTO> filtrarPersonal(String nombre, CargoPersonal cargoPersonal, EstadoPersonal estadoPersonal) {
        String nombreFiltro = (nombre != null && !nombre.isBlank()) ? nombre : null;
        List<Personal> personal = personalRepository.findByFilter(nombreFiltro,cargoPersonal,estadoPersonal);
        return personalMapper.toDTOList(personal);

    }
}