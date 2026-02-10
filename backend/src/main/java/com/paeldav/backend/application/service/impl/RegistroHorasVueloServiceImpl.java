package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloCreateDTO;
import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloDTO;
import com.paeldav.backend.application.mapper.RegistroHorasVueloMapper;
import com.paeldav.backend.application.service.base.RegistroHorasVueloService;
import com.paeldav.backend.domain.entity.RegistroHorasVuelo;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.exception.AsignacionInvalidaException;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.RegistroHorasVueloRepository;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Implementación del servicio de gestión de registros de horas de vuelo.
 * Maneja la creación, validación y cálculo de horas voladas por tripulantes.
 */
@Service
@RequiredArgsConstructor
public class RegistroHorasVueloServiceImpl implements RegistroHorasVueloService {

    private final RegistroHorasVueloRepository registroRepository;
    private final TripulanteRepository tripulanteRepository;
    private final VueloRepository vueloRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroHorasVueloMapper registroMapper;

    @Override
    @Transactional
    public RegistroHorasVueloDTO crearRegistro(RegistroHorasVueloCreateDTO registroCreateDTO) {
        // Validar que el tripulante existe
        Tripulante tripulante = tripulanteRepository.findById(registroCreateDTO.getTripulanteId())
                .orElseThrow(() -> new TripulanteNoEncontradoException(
                        "Tripulante no encontrado con ID: " + registroCreateDTO.getTripulanteId()
                ));

        // Validar que el vuelo existe
        Vuelo vuelo = vueloRepository.findById(registroCreateDTO.getVueloId())
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + registroCreateDTO.getVueloId()
                ));

        // Validar que el tripulante está asignado al vuelo
        boolean tripulanteAsignado = vuelo.getTripulacion() != null &&
                vuelo.getTripulacion().stream()
                        .anyMatch(t -> t.getId().equals(tripulante.getId()));

        if (!tripulanteAsignado) {
            throw new AsignacionInvalidaException(
                    "El tripulante " + tripulante.getNumeroLicencia() +
                    " no está asignado al vuelo " + vuelo.getId()
            );
        }

        // Convertir DTO a entidad
        RegistroHorasVuelo registro = registroMapper.toEntity(registroCreateDTO);
        registro.setTripulante(tripulante);
        registro.setVuelo(vuelo);

        // Guardar
        registro = registroRepository.save(registro);

        return registroMapper.toDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroHorasVueloDTO obtenerRegistroPorId(Long id) {
        RegistroHorasVuelo registro = registroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registro de horas de vuelo no encontrado con ID: " + id
                ));

        return registroMapper.toDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroHorasVueloDTO> obtenerRegistrosPorTripulante(Long tripulanteId) {
        // Validar que el tripulante existe
        if (!tripulanteRepository.existsById(tripulanteId)) {
            throw new TripulanteNoEncontradoException(
                    "Tripulante no encontrado con ID: " + tripulanteId
            );
        }

        List<RegistroHorasVuelo> registros = registroRepository.findByTripulanteId(tripulanteId);
        return registroMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroHorasVueloDTO> obtenerRegistrosPorVuelo(Long vueloId) {
        // Validar que el vuelo existe
        if (!vueloRepository.existsById(vueloId)) {
            throw new VueloNoEncontradoException(
                    "Vuelo no encontrado con ID: " + vueloId
            );
        }

        List<RegistroHorasVuelo> registros = registroRepository.findByVueloId(vueloId);
        return registroMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroHorasVueloDTO> obtenerRegistrosPendientes() {
        List<RegistroHorasVuelo> registros = registroRepository.findByAprobado(false);
        return registroMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularHorasTotales(Long tripulanteId) {
        // Validar que el tripulante existe
        if (!tripulanteRepository.existsById(tripulanteId)) {
            throw new TripulanteNoEncontradoException(
                    "Tripulante no encontrado con ID: " + tripulanteId
            );
        }

        Double horas = registroRepository.sumHorasByTripulanteId(tripulanteId);
        return horas != null ? horas : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularHorasMensuales(Long tripulanteId, LocalDate fecha) {
        // Validar que el tripulante existe
        if (!tripulanteRepository.existsById(tripulanteId)) {
            throw new TripulanteNoEncontradoException(
                    "Tripulante no encontrado con ID: " + tripulanteId
            );
        }

        // Calcular inicio y fin del mes
        YearMonth yearMonth = YearMonth.from(fecha);
        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fin = yearMonth.atEndOfMonth();

        Double horas = registroRepository.sumHorasByTripulanteIdAndFechaBetween(
                tripulanteId,
                inicio.atStartOfDay(),
                fin.atTime(23, 59, 59)
        );

        return horas != null ? horas : 0.0;
    }

    @Override
    @Transactional
    public RegistroHorasVueloDTO aprobarRegistro(Long registroId, Long usuarioAprobadorId) {
        RegistroHorasVuelo registro = registroRepository.findById(registroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registro de horas de vuelo no encontrado con ID: " + registroId
                ));

        // Validar que no esté ya aprobado
        if (Boolean.TRUE.equals(registro.getAprobado())) {
            throw new IllegalArgumentException(
                    "El registro ya ha sido aprobado anteriormente"
            );
        }

        // Obtener el usuario aprobador
        Usuario usuarioAprobador = usuarioRepository.findById(usuarioAprobadorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario aprobador no encontrado con ID: " + usuarioAprobadorId
                ));

        // Aprobar registro
        registro.setAprobado(true);
        registro.setAprobadoPor(usuarioAprobador);

        // Actualizar horas del tripulante
        Tripulante tripulante = registro.getTripulante();
        Double horasActuales = tripulante.getHorasVueloTotales() != null ?
                tripulante.getHorasVueloTotales() : 0.0;
        tripulante.setHorasVueloTotales(horasActuales + registro.getHorasVoladas());

        // Actualizar horas mensuales
        LocalDate fecha = registro.getFechaRegistro().toLocalDate();
        Double horasMensualActuales = calcularHorasMensuales(tripulante.getId(), fecha);
        tripulante.setHorasVueloMes(horasMensualActuales + registro.getHorasVoladas());

        tripulanteRepository.save(tripulante);
        registro = registroRepository.save(registro);

        return registroMapper.toDTO(registro);
    }

    @Override
    @Transactional
    public void eliminarRegistro(Long registroId) {
        RegistroHorasVuelo registro = registroRepository.findById(registroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registro de horas de vuelo no encontrado con ID: " + registroId
                ));

        // Validar que no esté aprobado
        if (Boolean.TRUE.equals(registro.getAprobado())) {
            throw new IllegalArgumentException(
                    "No se pueden eliminar registros aprobados"
            );
        }

        registroRepository.deleteById(registroId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerHorasEnVuelo(Long tripulanteId, Long vueloId) {
        // Validar que el tripulante existe
        if (!tripulanteRepository.existsById(tripulanteId)) {
            throw new TripulanteNoEncontradoException(
                    "Tripulante no encontrado con ID: " + tripulanteId
            );
        }

        // Validar que el vuelo existe
        if (!vueloRepository.existsById(vueloId)) {
            throw new VueloNoEncontradoException(
                    "Vuelo no encontrado con ID: " + vueloId
            );
        }

        List<RegistroHorasVuelo> registros = registroRepository.findByTripulanteIdAndAprobado(
                tripulanteId, true
        );

        return registros.stream()
                .filter(r -> r.getVuelo().getId().equals(vueloId))
                .mapToDouble(RegistroHorasVuelo::getHorasVoladas)
                .sum();
    }
}
