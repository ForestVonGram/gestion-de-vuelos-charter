package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.*;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.mapper.TripulanteMapper;
import com.paeldav.backend.application.service.base.DisponibilidadOperativaService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.ConflictoDisponibilidadException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de disponibilidad operativa.
 * Gestiona la consulta de disponibilidad y validación de conflictos de agenda.
 */
@Service
@RequiredArgsConstructor
public class DisponibilidadOperativaServiceImpl implements DisponibilidadOperativaService {

    private final VueloRepository vueloRepository;
    private final AeronaveRepository aeronaveRepository;
    private final TripulanteRepository tripulanteRepository;
    private final AeronaveMapper aeronaveMapper;
    private final TripulanteMapper tripulanteMapper;

    /**
     * Estados de vuelo que se consideran activos y pueden generar conflictos.
     */
    private static final List<EstadoVuelo> ESTADOS_VUELO_ACTIVOS = List.of(
            EstadoVuelo.SOLICITADO,
            EstadoVuelo.CONFIRMADO,
            EstadoVuelo.EN_CURSO
    );

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadAeronaveDTO consultarDisponibilidadAeronave(
            Long aeronaveId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        Aeronave aeronave = aeronaveRepository.findById(aeronaveId)
                .orElseThrow(() -> new IllegalArgumentException("Aeronave no encontrada con ID: " + aeronaveId));

        // Verificar estado base de la aeronave
        boolean estadoPermiteOperacion = aeronave.getEstado() == EstadoAeronave.DISPONIBLE;

        // Buscar vuelos que se solapan con el rango
        List<Vuelo> vuelosEnConflicto = vueloRepository.findVuelosEnRangoPorAeronave(
                aeronaveId, fechaInicio, fechaFin, ESTADOS_VUELO_ACTIVOS);

        List<ConflictoAgendaDTO> conflictos = vuelosEnConflicto.stream()
                .map(this::convertirVueloAConflicto)
                .collect(Collectors.toList());

        String motivoNoDisponible = null;
        if (!estadoPermiteOperacion) {
            motivoNoDisponible = "La aeronave se encuentra en estado: " + aeronave.getEstado();
        } else if (!conflictos.isEmpty()) {
            motivoNoDisponible = "La aeronave tiene " + conflictos.size() + " vuelo(s) programado(s) en el rango solicitado";
        }

        return DisponibilidadAeronaveDTO.builder()
                .aeronaveId(aeronaveId)
                .matricula(aeronave.getMatricula())
                .modelo(aeronave.getModelo())
                .estadoActual(aeronave.getEstado())
                .disponible(estadoPermiteOperacion && conflictos.isEmpty())
                .fechaConsultaInicio(fechaInicio)
                .fechaConsultaFin(fechaFin)
                .conflictos(conflictos)
                .motivoNoDisponible(motivoNoDisponible)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadTripulanteDTO consultarDisponibilidadTripulante(
            Long tripulanteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        Tripulante tripulante = tripulanteRepository.findById(tripulanteId)
                .orElseThrow(() -> new IllegalArgumentException("Tripulante no encontrado con ID: " + tripulanteId));

        // Verificar estado base del tripulante
        boolean estadoPermiteOperacion = tripulante.getEstado() == EstadoTripulante.DISPONIBLE;

        // Buscar vuelos que se solapan con el rango
        List<Vuelo> vuelosEnConflicto = vueloRepository.findVuelosEnRangoPorTripulante(
                tripulanteId, fechaInicio, fechaFin, ESTADOS_VUELO_ACTIVOS);

        List<ConflictoAgendaDTO> conflictos = vuelosEnConflicto.stream()
                .map(this::convertirVueloAConflicto)
                .collect(Collectors.toList());

        String nombreCompleto = tripulante.getUsuario() != null
                ? tripulante.getUsuario().getNombre() + " " + tripulante.getUsuario().getApellido()
                : "Sin nombre";

        String motivoNoDisponible = null;
        if (!estadoPermiteOperacion) {
            motivoNoDisponible = "El tripulante se encuentra en estado: " + tripulante.getEstado();
        } else if (!conflictos.isEmpty()) {
            motivoNoDisponible = "El tripulante tiene " + conflictos.size() + " vuelo(s) asignado(s) en el rango solicitado";
        }

        return DisponibilidadTripulanteDTO.builder()
                .tripulanteId(tripulanteId)
                .nombreCompleto(nombreCompleto)
                .numeroLicencia(tripulante.getNumeroLicencia())
                .esPiloto(tripulante.getEsPiloto() != null && tripulante.getEsPiloto())
                .estadoActual(tripulante.getEstado())
                .disponible(estadoPermiteOperacion && conflictos.isEmpty())
                .fechaConsultaInicio(fechaInicio)
                .fechaConsultaFin(fechaFin)
                .conflictos(conflictos)
                .motivoNoDisponible(motivoNoDisponible)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AeronaveDTO> consultarAeronavesDisponibles(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Integer capacidadMinima) {

        // Obtener IDs de aeronaves que tienen vuelos en el rango
        List<Long> aeronaveIdsOcupadas = vueloRepository.findAeronaveIdsConVuelosEnRango(
                fechaInicio, fechaFin, ESTADOS_VUELO_ACTIVOS);

        // Obtener todas las aeronaves disponibles por estado
        List<Aeronave> aeronavesDisponibles = aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE);

        // Filtrar las que no tienen conflictos y cumplen capacidad mínima
        return aeronavesDisponibles.stream()
                .filter(a -> !aeronaveIdsOcupadas.contains(a.getId()))
                .filter(a -> capacidadMinima == null || a.getCapacidadPasajeros() >= capacidadMinima)
                .map(aeronaveMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripulanteDTO> consultarTripulantesDisponibles(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Boolean soloPilotos) {

        // Obtener IDs de tripulantes que tienen vuelos en el rango
        List<Long> tripulanteIdsOcupados = vueloRepository.findTripulanteIdsConVuelosEnRango(
                fechaInicio, fechaFin, ESTADOS_VUELO_ACTIVOS);

        // Obtener todos los tripulantes disponibles por estado
        List<Tripulante> tripulantesDisponibles = tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE);

        // Filtrar los que no tienen conflictos
        return tripulantesDisponibles.stream()
                .filter(t -> !tripulanteIdsOcupados.contains(t.getId()))
                .filter(t -> soloPilotos == null || !soloPilotos || (t.getEsPiloto() != null && t.getEsPiloto()))
                .map(tripulanteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoValidacionDTO validarConflictosAgenda(
            Long aeronaveId,
            List<Long> tripulantesIds,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        List<ConflictoAgendaDTO> conflictosAeronave = new ArrayList<>();
        List<ConflictoAgendaDTO> conflictosTripulacion = new ArrayList<>();

        // Validar conflictos de aeronave si se proporciona
        if (aeronaveId != null) {
            List<Vuelo> vuelosAeronave = vueloRepository.findVuelosEnRangoPorAeronave(
                    aeronaveId, fechaInicio, fechaFin, ESTADOS_VUELO_ACTIVOS);
            conflictosAeronave = vuelosAeronave.stream()
                    .map(v -> convertirVueloAConflictoConDescripcion(v, "Aeronave"))
                    .collect(Collectors.toList());
        }

        // Validar conflictos de cada tripulante
        if (tripulantesIds != null && !tripulantesIds.isEmpty()) {
            for (Long tripulanteId : tripulantesIds) {
                List<Vuelo> vuelosTripulante = vueloRepository.findVuelosEnRangoPorTripulante(
                        tripulanteId, fechaInicio, fechaFin, ESTADOS_VUELO_ACTIVOS);

                for (Vuelo vuelo : vuelosTripulante) {
                    Tripulante tripulante = tripulanteRepository.findById(tripulanteId).orElse(null);
                    String nombreTripulante = tripulante != null && tripulante.getUsuario() != null
                            ? tripulante.getUsuario().getNombre() + " " + tripulante.getUsuario().getApellido()
                            : "Tripulante ID: " + tripulanteId;
                    conflictosTripulacion.add(convertirVueloAConflictoConDescripcion(vuelo, nombreTripulante));
                }
            }
        }

        boolean disponible = conflictosAeronave.isEmpty() && conflictosTripulacion.isEmpty();

        String resumen = disponible
                ? "Todos los recursos están disponibles para el rango solicitado"
                : String.format("Se detectaron %d conflicto(s): %d de aeronave, %d de tripulación",
                    conflictosAeronave.size() + conflictosTripulacion.size(),
                    conflictosAeronave.size(),
                    conflictosTripulacion.size());

        return ResultadoValidacionDTO.builder()
                .disponible(disponible)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .conflictosAeronave(conflictosAeronave)
                .conflictosTripulacion(conflictosTripulacion)
                .resumen(resumen)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public void validarYLanzarSiHayConflictos(
            Long aeronaveId,
            List<Long> tripulantesIds,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        ResultadoValidacionDTO resultado = validarConflictosAgenda(
                aeronaveId, tripulantesIds, fechaInicio, fechaFin);

        if (!resultado.isDisponible()) {
            throw new ConflictoDisponibilidadException(
                    "Conflicto de disponibilidad detectado: " + resultado.getResumen(),
                    resultado);
        }
    }

    /**
     * Convierte un vuelo en un DTO de conflicto de agenda.
     */
    private ConflictoAgendaDTO convertirVueloAConflicto(Vuelo vuelo) {
        return ConflictoAgendaDTO.builder()
                .vueloId(vuelo.getId())
                .origen(vuelo.getOrigen())
                .destino(vuelo.getDestino())
                .fechaSalida(vuelo.getFechaSalidaProgramada())
                .fechaLlegada(vuelo.getFechaLlegadaProgramada())
                .estadoVuelo(vuelo.getEstado())
                .descripcion(String.format("Vuelo %s → %s", vuelo.getOrigen(), vuelo.getDestino()))
                .build();
    }

    /**
     * Convierte un vuelo en un DTO de conflicto con descripción del recurso afectado.
     */
    private ConflictoAgendaDTO convertirVueloAConflictoConDescripcion(Vuelo vuelo, String recursoAfectado) {
        return ConflictoAgendaDTO.builder()
                .vueloId(vuelo.getId())
                .origen(vuelo.getOrigen())
                .destino(vuelo.getDestino())
                .fechaSalida(vuelo.getFechaSalidaProgramada())
                .fechaLlegada(vuelo.getFechaLlegadaProgramada())
                .estadoVuelo(vuelo.getEstado())
                .descripcion(String.format("%s asignado(a) a vuelo %s → %s",
                        recursoAfectado, vuelo.getOrigen(), vuelo.getDestino()))
                .build();
    }
}
