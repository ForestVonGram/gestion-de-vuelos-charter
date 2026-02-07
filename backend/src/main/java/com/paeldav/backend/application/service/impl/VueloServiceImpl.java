package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.dto.vuelo.VueloUpdateDTO;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.base.VueloService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.exception.VueloEstadoInvalidoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Implementación del servicio de gestión de agendamiento de vuelos.
 * Maneja la lógica de negocio para crear, actualizar, cancelar y cambiar el estado de vuelos.
 */
@Service
@RequiredArgsConstructor
public class VueloServiceImpl implements VueloService {

    private final VueloRepository vueloRepository;
    private final UsuarioRepository usuarioRepository;
    private final VueloMapper vueloMapper;

    @Override
    @Transactional
    public VueloDTO crearVuelo(VueloCreateDTO vueloCreateDTO) {
        // Validar que el usuario solicitante existe
        Usuario usuario = usuarioRepository.findById(vueloCreateDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + vueloCreateDTO.getUsuarioId()
                ));

        // Convertir DTO a entidad
        Vuelo vuelo = vueloMapper.toEntity(vueloCreateDTO);

        // Asignar usuario y estado inicial
        vuelo.setUsuario(usuario);
        vuelo.setEstado(EstadoVuelo.SOLICITADO);

        // Guardar en base de datos
        vuelo = vueloRepository.save(vuelo);

        return vueloMapper.toDTO(vuelo);
    }

    @Override
    @Transactional(readOnly = true)
    public VueloDTO obtenerVueloPorId(Long id) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + id
                ));

        return vueloMapper.toDTO(vuelo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VueloDTO> obtenerTodosVuelos() {
        List<Vuelo> vuelos = vueloRepository.findAll();
        return vueloMapper.toDTOList(vuelos);
    }

    @Override
    @Transactional
    public VueloDTO actualizarVuelo(Long id, VueloUpdateDTO vueloUpdateDTO) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + id
                ));

        // Actualizar campos (el mapper ignora nulos)
        vueloMapper.updateEntityFromDTO(vueloUpdateDTO, vuelo);

        vuelo = vueloRepository.save(vuelo);

        return vueloMapper.toDTO(vuelo);
    }

    @Override
    @Transactional
    public void cancelarVuelo(Long id) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + id
                ));

        // Validar que el vuelo pueda ser cancelado
        if (vuelo.getEstado() == EstadoVuelo.COMPLETADO) {
            throw new VueloEstadoInvalidoException(
                    "No se puede cancelar un vuelo que ya ha sido completado"
            );
        }

        if (vuelo.getEstado() == EstadoVuelo.CANCELADO) {
            throw new VueloEstadoInvalidoException(
                    "El vuelo ya ha sido cancelado anteriormente"
            );
        }

        // Cambiar estado a cancelado
        vuelo.setEstado(EstadoVuelo.CANCELADO);
        vueloRepository.save(vuelo);
    }

    @Override
    @Transactional
    public VueloDTO cambiarEstadoVuelo(Long id, EstadoVuelo nuevoEstado) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + id
                ));

        // Validar transición de estado
        validarTransicionEstado(vuelo.getEstado(), nuevoEstado);

        // Cambiar estado
        vuelo.setEstado(nuevoEstado);
        vuelo = vueloRepository.save(vuelo);

        return vueloMapper.toDTO(vuelo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VueloDTO> obtenerVuelosPorEstado(EstadoVuelo estado) {
        List<Vuelo> vuelos = vueloRepository.findByEstado(estado);
        return vueloMapper.toDTOList(vuelos);
    }

    /**
     * Valida que la transición de estado sea permitida.
     * Máquina de estados:
     * - SOLICITADO -> CONFIRMADO, CANCELADO
     * - CONFIRMADO -> EN_CURSO, CANCELADO
     * - EN_CURSO -> COMPLETADO, CANCELADO
     * - COMPLETADO -> (sin transiciones permitidas)
     * - CANCELADO -> (sin transiciones permitidas)
     *
     * @param estadoActual estado actual del vuelo
     * @param nuevoEstado estado destino
     * @throws VueloEstadoInvalidoException si la transición no es válida
     */
    private void validarTransicionEstado(EstadoVuelo estadoActual, EstadoVuelo nuevoEstado) {
        // Estados finales no pueden cambiar
        if (estadoActual == EstadoVuelo.COMPLETADO || estadoActual == EstadoVuelo.CANCELADO) {
            throw new VueloEstadoInvalidoException(
                    String.format("No se puede cambiar el estado de un vuelo %s", estadoActual)
            );
        }

        // No se puede cambiar al mismo estado
        if (estadoActual == nuevoEstado) {
            throw new VueloEstadoInvalidoException(
                    String.format("El vuelo ya está en estado %s", estadoActual)
            );
        }

        // Validar transiciones permitidas por estado actual
        boolean transicionValida = switch (estadoActual) {
            case SOLICITADO -> nuevoEstado == EstadoVuelo.CONFIRMADO || nuevoEstado == EstadoVuelo.CANCELADO;
            case CONFIRMADO -> nuevoEstado == EstadoVuelo.EN_CURSO || nuevoEstado == EstadoVuelo.CANCELADO;
            case EN_CURSO -> nuevoEstado == EstadoVuelo.COMPLETADO || nuevoEstado == EstadoVuelo.CANCELADO;
            case COMPLETADO, CANCELADO -> false;
        };

        if (!transicionValida) {
            throw new VueloEstadoInvalidoException(
                    String.format("No se puede cambiar de %s a %s", estadoActual, nuevoEstado)
            );
        }
    }
}
