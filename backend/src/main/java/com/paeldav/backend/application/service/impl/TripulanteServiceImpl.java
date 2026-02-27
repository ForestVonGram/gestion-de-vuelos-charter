package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.tripulante.TripulanteCreateDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteUpdateDTO;
import com.paeldav.backend.application.mapper.TripulanteMapper;
import com.paeldav.backend.application.service.base.TripulanteService;
import com.paeldav.backend.application.service.integration.ValidadorCertificacionesService;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.TripulanteYaExisteException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de tripulantes.
 */
@Service
@RequiredArgsConstructor
public class TripulanteServiceImpl implements TripulanteService {

    private final TripulanteRepository tripulanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TripulanteMapper tripulanteMapper;
    private final ValidadorCertificacionesService validadorCertificaciones;

    @Override
    @Transactional
    public TripulanteDTO registrarTripulante(TripulanteCreateDTO tripulanteCreateDTO) {
        // Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(tripulanteCreateDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + tripulanteCreateDTO.getUsuarioId()
                ));

        // Verificar que el número de licencia sea único
        if (tripulanteRepository.existsByNumeroLicencia(tripulanteCreateDTO.getNumeroLicencia())) {
            throw new TripulanteYaExisteException(
                    "Ya existe un tripulante con el número de licencia: " + tripulanteCreateDTO.getNumeroLicencia()
            );
        }

        // Convertir DTO a entidad
        Tripulante tripulante = tripulanteMapper.toEntity(tripulanteCreateDTO);
        tripulante.setUsuario(usuario);

        // Guardar en base de datos
        tripulante = tripulanteRepository.save(tripulante);

        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public TripulanteDTO obtenerTripulantePorId(Long id) {
        Tripulante tripulante = tripulanteRepository.findByUsuarioId(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public TripulanteDTO obtenerTripulantePorNumeroLicencia(String numeroLicencia) {
        Tripulante tripulante = tripulanteRepository.findByNumeroLicencia(numeroLicencia)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con licencia: " + numeroLicencia
                ));
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerTodosTripulantes() {
        List<Tripulante> tripulantes = tripulanteRepository.findAll();
        return tripulanteMapper.toDTOList(tripulantes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerPilotos() {
        List<Tripulante> pilotos = tripulanteRepository.findAll().stream()
                .filter(t -> t.getEsPiloto() != null && t.getEsPiloto())
                .collect(Collectors.toList());
        return tripulanteMapper.toDTOList(pilotos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerAuxiliares() {
        List<Tripulante> auxiliares = tripulanteRepository.findAll().stream()
                .filter(t -> t.getEsPiloto() == null || !t.getEsPiloto())
                .collect(Collectors.toList());
        return tripulanteMapper.toDTOList(auxiliares);
    }

    @Override
    @Transactional
    public TripulanteDTO editarTripulante(Long id, TripulanteUpdateDTO tripulanteUpdateDTO) {
        Tripulante tripulante = tripulanteRepository.findById(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));

        // Actualizar campos
        tripulanteMapper.updateEntityFromUpdateDTO(tripulanteUpdateDTO, tripulante);

        tripulante = tripulanteRepository.save(tripulante);
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional
    public void eliminarTripulante(Long id) {
        Tripulante tripulante = tripulanteRepository.findById(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));
        tripulanteRepository.delete(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarTripulante(Long id) {
        Tripulante tripulante = tripulanteRepository.findById(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));
        validadorCertificaciones.validarTripulanteCompleto(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerTripulantesDisponibles() {
        List<Tripulante> disponibles = tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE);
        return tripulanteMapper.toDTOList(disponibles);
    }
}
