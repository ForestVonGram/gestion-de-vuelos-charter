package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.incidencia.IncidenciaCreateDTO;
import com.paeldav.backend.application.dto.incidencia.IncidenciaDTO;
import com.paeldav.backend.application.mapper.IncidenciaMapper;
import com.paeldav.backend.application.service.base.IncidenciaService;
import com.paeldav.backend.domain.entity.Incidencia;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.exception.IncidenciaNoEncontradaException;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.IncidenciaRepository;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de incidencias técnicas.
 * Proporciona la lógica de negocio para reportar, gestionar y resolver incidencias de vuelos.
 */
@Service
@RequiredArgsConstructor
public class IncidenciaServiceImpl implements IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final VueloRepository vueloRepository;
    private final TripulanteRepository tripulanteRepository;
    private final IncidenciaMapper incidenciaMapper;

    @Override
    public IncidenciaDTO reportarIncidencia(IncidenciaCreateDTO incidenciaCreateDTO) {
        // Validar existencia del vuelo
        Vuelo vuelo = vueloRepository.findById(incidenciaCreateDTO.getVueloId())
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + incidenciaCreateDTO.getVueloId()));

        // Validar existencia del tripulante que reporta
        Tripulante reportadoPor = tripulanteRepository.findById(incidenciaCreateDTO.getReportadoPorId())
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + incidenciaCreateDTO.getReportadoPorId()));

        // Crear la incidencia
        Incidencia incidencia = incidenciaMapper.toEntity(incidenciaCreateDTO);
        incidencia.setVuelo(vuelo);
        incidencia.setReportadoPor(reportadoPor);
        incidencia.setResuelta(false);

        // Guardar la incidencia
        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        return incidenciaMapper.toDTO(incidenciaGuardada);
    }

    @Override
    public IncidenciaDTO obtenerIncidenciaPorId(Long id) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException(
                        "Incidencia no encontrada con ID: " + id));

        return incidenciaMapper.toDTO(incidencia);
    }

    @Override
    public List<IncidenciaDTO> obtenerTodasIncidencias() {
        List<Incidencia> incidencias = incidenciaRepository.findAll();
        return incidenciaMapper.toDTOList(incidencias);
    }

    @Override
    public List<IncidenciaDTO> obtenerIncidenciasPorVuelo(Long vueloId) {
        // Validar existencia del vuelo
        if (!vueloRepository.existsById(vueloId)) {
            throw new VueloNoEncontradoException(
                    "Vuelo no encontrado con ID: " + vueloId);
        }

        List<Incidencia> incidencias = incidenciaRepository.findByVueloId(vueloId);
        return incidenciaMapper.toDTOList(incidencias);
    }

    @Override
    public List<IncidenciaDTO> obtenerIncidenciasPendientes() {
        List<Incidencia> incidencias = incidenciaRepository.findPendientes();
        return incidenciaMapper.toDTOList(incidencias);
    }

    @Override
    public List<IncidenciaDTO> obtenerIncidenciasNoResueltas() {
        List<Incidencia> incidencias = incidenciaRepository.findByResuelta(false);
        return incidenciaMapper.toDTOList(incidencias);
    }

    @Override
    public List<IncidenciaDTO> obtenerIncidenciasPorGravedad(String gravedad) {
        List<Incidencia> todas = incidenciaRepository.findAll();
        return todas.stream()
                .filter(i -> i.getGravedad() != null && i.getGravedad().equalsIgnoreCase(gravedad))
                .map(incidenciaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidenciaDTO> obtenerIncidenciasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        List<Incidencia> incidencias = incidenciaRepository.findByFechaReporteBetween(inicio, fin);
        return incidenciaMapper.toDTOList(incidencias);
    }

    @Override
    public IncidenciaDTO resolverIncidencia(Long id, String accionesTomadas) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException(
                        "Incidencia no encontrada con ID: " + id));

        incidencia.setResuelta(true);
        incidencia.setFechaResolucion(LocalDateTime.now());
        incidencia.setAccionesTomadas(accionesTomadas);

        Incidencia incidenciaActualizada = incidenciaRepository.save(incidencia);

        return incidenciaMapper.toDTO(incidenciaActualizada);
    }
}
