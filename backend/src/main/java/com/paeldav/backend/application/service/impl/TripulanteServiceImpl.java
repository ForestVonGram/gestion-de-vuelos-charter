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
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.TripulanteYaExisteException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de tripulantes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TripulanteServiceImpl implements TripulanteService {

    private final TripulanteRepository tripulanteRepository; // Repositorio de tripulantes
    private final UsuarioRepository usuarioRepository; // Repositorio de usuarios
    private final TripulanteMapper tripulanteMapper; // Mapper de tripulantes
    private final ValidadorCertificacionesService validadorCertificaciones; // Servicio para validar certificaciones

    @Override
    @Transactional
    public TripulanteDTO registrarTripulante(TripulanteCreateDTO tripulanteCreateDTO) {
        log.info("Registrando nuevo tripulante - Licencia: {}, Usuario ID: {}",
                tripulanteCreateDTO.getNumeroLicencia(), tripulanteCreateDTO.getUsuarioId());
        // Registra un nuevo tripulante en el sistema

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
        usuario.setRol(RolUsuario.TRIPULACION);
        // Guardar en base de datos
        tripulante = tripulanteRepository.save(tripulante);

        log.info("Tripulante registrado - ID: {}, Licencia: {}", tripulante.getId(), tripulante.getNumeroLicencia());
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public TripulanteDTO obtenerTripulantePorId(Long id) {
        // Obtiene un tripulante por su ID de usuario
        Tripulante tripulante = tripulanteRepository.findByUsuarioId(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public TripulanteDTO obtenerTripulantePorNumeroLicencia(String numeroLicencia) {
        // Obtiene un tripulante por su número de licencia
        Tripulante tripulante = tripulanteRepository.findByNumeroLicencia(numeroLicencia)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con licencia: " + numeroLicencia
                ));
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerTodosTripulantes() {
        // Obtiene todos los tripulantes registrados
        List<Tripulante> tripulantes = tripulanteRepository.findAll();
        return tripulanteMapper.toDTOList(tripulantes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerPilotos() {
        // Obtiene todos los tripulantes que son pilotos
        List<Tripulante> pilotos = tripulanteRepository.findAll().stream()
                .filter(t -> t.getEsPiloto() != null && t.getEsPiloto())
                .collect(Collectors.toList());
        return tripulanteMapper.toDTOList(pilotos);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripulanteDTO> obtenerAuxiliares(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Tripulante> auxiliares = tripulanteRepository.findAll(pageable);
        return auxiliares.map(tripulanteMapper::toDTO);
    }

    @Override
    @Transactional
    public TripulanteDTO editarTripulante(Long id, TripulanteUpdateDTO tripulanteUpdateDTO) {
        log.info("Editando tripulante ID: {}", id);
        // Edita los datos de un tripulante existente
        Tripulante tripulante = tripulanteRepository.findById(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));

        // Actualizar campos
        tripulanteMapper.updateEntityFromUpdateDTO(tripulanteUpdateDTO, tripulante);

        tripulante = tripulanteRepository.save(tripulante);
        log.info("Tripulante ID: {} actualizado correctamente", id);
        return tripulanteMapper.toDTO(tripulante);
    }

    @Override
    @Transactional
    public void eliminarTripulante(Long id) {
        log.info("Eliminando tripulante ID: {}", id);
        // Elimina un tripulante del sistema
        Tripulante tripulante = tripulanteRepository.findById(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));
        tripulanteRepository.delete(tripulante);
        log.info("Tripulante ID: {} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarTripulante(Long id) {
        log.info("Validando tripulante ID: {}", id);
        // Valida que un tripulante cumpla con todas las certificaciones requeridas
        Tripulante tripulante = tripulanteRepository.findById(id)
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + id
                ));
        validadorCertificaciones.validarTripulanteCompleto(tripulante);
        log.info("Tripulante ID: {} validado correctamente", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> obtenerTripulantesDisponibles() {
        // Obtiene todos los tripulantes que están disponibles para asignar a vuelos
        List<Tripulante> disponibles = tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE);
        return tripulanteMapper.toDTOList(disponibles);
    }
}