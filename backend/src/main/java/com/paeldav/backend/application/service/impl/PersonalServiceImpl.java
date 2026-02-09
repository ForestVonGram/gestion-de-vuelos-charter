package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.personal.PersonalCreateDTO;
import com.paeldav.backend.application.dto.personal.PersonalDTO;
import com.paeldav.backend.application.dto.personal.PersonalUpdateDTO;
import com.paeldav.backend.application.mapper.PersonalMapper;
import com.paeldav.backend.application.service.base.PersonalService;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.entity.Usuario;
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
 * Implementación del servicio de gestión de personal.
 */
@Service
@RequiredArgsConstructor
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonalMapper personalMapper;

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

    @Override
    @Transactional(readOnly = true)
    public PersonalDTO obtenerPersonalPorId(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));
        return personalMapper.toDTO(personal);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalDTO obtenerPersonalPorNumeroEmpleado(String numeroEmpleado) {
        Personal personal = personalRepository.findByNumeroEmpleado(numeroEmpleado)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con número de empleado: " + numeroEmpleado
                ));
        return personalMapper.toDTO(personal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalDTO> obtenerTodoPersonal() {
        List<Personal> personal = personalRepository.findAll();
        return personalMapper.toDTOList(personal);
    }

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

    @Override
    @Transactional
    public void eliminarPersonal(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new PersonalNoEncontradoException(
                        "Personal no encontrado con ID: " + id
                ));
        personalRepository.delete(personal);
    }

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
}
