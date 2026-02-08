package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.vuelo.*;
import com.paeldav.backend.application.mapper.HistorialVueloMapper;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.base.VueloService;
import com.paeldav.backend.application.service.base.PagoService;
import com.paeldav.backend.domain.entity.*;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de gestión de agendamiento de vuelos.
 * Maneja la lógica de negocio para crear, actualizar, cancelar y cambiar el estado de vuelos.
 */
@Service
@RequiredArgsConstructor
public class VueloServiceImpl implements VueloService {

    private final VueloRepository vueloRepository;
    private final UsuarioRepository usuarioRepository;
    private final AeronaveRepository aeronaveRepository;
    private final TripulanteRepository tripulanteRepository;
    private final HistorialVueloRepository historialVueloRepository;
    private final PagoService pagoService;
    private final VueloMapper vueloMapper;
    private final HistorialVueloMapper historialVueloMapper;

    private static final List<EstadoVuelo> ESTADOS_ACTIVOS = List.of(
            EstadoVuelo.SOLICITADO, EstadoVuelo.CONFIRMADO, EstadoVuelo.EN_CURSO
    );

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

        // Validar que el vuelo tiene pago confirmado antes de pasar a EN_CURSO
        if (nuevoEstado == EstadoVuelo.EN_CURSO && vuelo.getCostoEstimado() != null) {
            if (!pagoService.tienePagoConfirmado(vuelo.getId(), vuelo.getCostoEstimado())) {
                throw new IllegalStateException(
                        "No se puede iniciar un vuelo sin pagos confirmados. " +
                        "Costo estimado: " + vuelo.getCostoEstimado() + 
                        ", Pagos confirmados: " + pagoService.obtenerTotalPagosConfirmados(vuelo.getId())
                );
            }
        }

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

    // ==================== APROBACIÓN Y RECHAZO ====================

    @Override
    @Transactional
    public VueloDTO aprobarSolicitud(Long vueloId, SolicitudAprobacionDTO dto) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + vueloId
                ));

        // Validar que esté en estado SOLICITADO
        if (vuelo.getEstado() != EstadoVuelo.SOLICITADO) {
            throw new VueloEstadoInvalidoException(
                    "Solo se pueden aprobar vuelos en estado SOLICITADO. Estado actual: " + vuelo.getEstado()
            );
        }

        EstadoVuelo estadoAnterior = vuelo.getEstado();

        // Cambiar estado a CONFIRMADO
        vuelo.setEstado(EstadoVuelo.CONFIRMADO);

        // Asignar costo estimado si se proporciona
        if (dto != null && dto.getCostoEstimado() != null) {
            vuelo.setCostoEstimado(dto.getCostoEstimado());
        }

        vuelo = vueloRepository.save(vuelo);

        // Registrar en historial
        registrarHistorial(vuelo, estadoAnterior, EstadoVuelo.CONFIRMADO, "APROBACION",
                dto != null ? dto.getMotivo() : "Solicitud aprobada");

        return vueloMapper.toDTO(vuelo);
    }

    @Override
    @Transactional
    public VueloDTO rechazarSolicitud(Long vueloId, SolicitudRechazoDTO dto) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + vueloId
                ));

        // Validar que esté en estado SOLICITADO
        if (vuelo.getEstado() != EstadoVuelo.SOLICITADO) {
            throw new VueloEstadoInvalidoException(
                    "Solo se pueden rechazar vuelos en estado SOLICITADO. Estado actual: " + vuelo.getEstado()
            );
        }

        EstadoVuelo estadoAnterior = vuelo.getEstado();

        // Cambiar estado a CANCELADO
        vuelo.setEstado(EstadoVuelo.CANCELADO);
        vuelo = vueloRepository.save(vuelo);

        // Registrar en historial con motivo obligatorio
        registrarHistorial(vuelo, estadoAnterior, EstadoVuelo.CANCELADO, "RECHAZO", dto.getMotivo());

        return vueloMapper.toDTO(vuelo);
    }

    // ==================== ASIGNACIÓN DE RECURSOS ====================

    @Override
    @Transactional
    public VueloDTO asignarAeronave(Long vueloId, AsignacionAeronaveDTO dto) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + vueloId
                ));

        // Validar estado del vuelo (solo SOLICITADO o CONFIRMADO)
        if (vuelo.getEstado() != EstadoVuelo.SOLICITADO && vuelo.getEstado() != EstadoVuelo.CONFIRMADO) {
            throw new VueloEstadoInvalidoException(
                    "Solo se puede asignar aeronave a vuelos en estado SOLICITADO o CONFIRMADO"
            );
        }

        Aeronave aeronave = aeronaveRepository.findById(dto.getAeronaveId())
                .orElseThrow(() -> new AeronaveNoEncontradaException(
                        "Aeronave no encontrada con ID: " + dto.getAeronaveId()
                ));

        // Validar estado de la aeronave
        if (aeronave.getEstado() != EstadoAeronave.DISPONIBLE) {
            throw new AsignacionInvalidaException(
                    "La aeronave no está disponible. Estado actual: " + aeronave.getEstado()
            );
        }

        // Validar capacidad de pasajeros
        if (vuelo.getNumeroPasajeros() != null && 
            aeronave.getCapacidadPasajeros() < vuelo.getNumeroPasajeros()) {
            throw new AsignacionInvalidaException(
                    String.format("La aeronave tiene capacidad para %d pasajeros, pero el vuelo requiere %d",
                            aeronave.getCapacidadPasajeros(), vuelo.getNumeroPasajeros())
            );
        }

        // Validar conflictos de horario
        List<Vuelo> vuelosConflicto = vueloRepository.findVuelosEnRangoPorAeronave(
                aeronave.getId(),
                vuelo.getFechaSalidaProgramada(),
                vuelo.getFechaLlegadaProgramada(),
                ESTADOS_ACTIVOS
        );

        // Excluir el vuelo actual si ya tenía esta aeronave asignada
        vuelosConflicto = vuelosConflicto.stream()
                .filter(v -> !v.getId().equals(vueloId))
                .toList();

        if (!vuelosConflicto.isEmpty()) {
            throw new ConflictoDisponibilidadException(
                    "La aeronave tiene conflictos de horario con otros vuelos programados"
            );
        }

        // Asignar aeronave
        vuelo.setAeronave(aeronave);

        // Agregar observaciones si se proporcionan
        if (dto.getObservaciones() != null && !dto.getObservaciones().isBlank()) {
            String obsActuales = vuelo.getObservaciones() != null ? vuelo.getObservaciones() + "\n" : "";
            vuelo.setObservaciones(obsActuales + "[Asignación aeronave] " + dto.getObservaciones());
        }

        vuelo = vueloRepository.save(vuelo);

        // Registrar en historial
        registrarHistorial(vuelo, vuelo.getEstado(), vuelo.getEstado(), "ASIGNACION_AERONAVE",
                "Aeronave asignada: " + aeronave.getMatricula());

        return vueloMapper.toDTO(vuelo);
    }

    @Override
    @Transactional
    public VueloDTO asignarTripulacion(Long vueloId, AsignacionTripulacionDTO dto) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
                .orElseThrow(() -> new VueloNoEncontradoException(
                        "Vuelo no encontrado con ID: " + vueloId
                ));

        // Validar estado del vuelo
        if (vuelo.getEstado() != EstadoVuelo.SOLICITADO && vuelo.getEstado() != EstadoVuelo.CONFIRMADO) {
            throw new VueloEstadoInvalidoException(
                    "Solo se puede asignar tripulación a vuelos en estado SOLICITADO o CONFIRMADO"
            );
        }

        List<Tripulante> tripulantes = new ArrayList<>();
        boolean tienePiloto = false;

        for (Long tripulanteId : dto.getTripulanteIds()) {
            Tripulante tripulante = tripulanteRepository.findById(tripulanteId)
                    .orElseThrow(() -> new TripulanteNoEncontradoException(
                            "Tripulante no encontrado con ID: " + tripulanteId
                    ));

            // Validar estado del tripulante
            if (tripulante.getEstado() != EstadoTripulante.DISPONIBLE) {
                throw new AsignacionInvalidaException(
                        String.format("El tripulante %s no está disponible. Estado: %s",
                                tripulante.getNumeroLicencia(), tripulante.getEstado())
                );
            }

            // Validar licencia vigente
            if (tripulante.getFechaVencimientoLicencia() != null &&
                tripulante.getFechaVencimientoLicencia().isBefore(LocalDate.now())) {
                throw new AsignacionInvalidaException(
                        String.format("El tripulante %s tiene la licencia vencida",
                                tripulante.getNumeroLicencia())
                );
            }

            // Validar conflictos de horario
            List<Vuelo> vuelosConflicto = vueloRepository.findVuelosEnRangoPorTripulante(
                    tripulanteId,
                    vuelo.getFechaSalidaProgramada(),
                    vuelo.getFechaLlegadaProgramada(),
                    ESTADOS_ACTIVOS
            );

            vuelosConflicto = vuelosConflicto.stream()
                    .filter(v -> !v.getId().equals(vueloId))
                    .toList();

            if (!vuelosConflicto.isEmpty()) {
                throw new ConflictoDisponibilidadException(
                        String.format("El tripulante %s tiene conflictos de horario",
                                tripulante.getNumeroLicencia())
                );
            }

            if (Boolean.TRUE.equals(tripulante.getEsPiloto())) {
                tienePiloto = true;
            }

            tripulantes.add(tripulante);
        }

        // Validar que haya al menos un piloto
        if (!tienePiloto) {
            throw new AsignacionInvalidaException(
                    "La tripulación debe incluir al menos un piloto"
            );
        }

        // Asignar tripulación
        vuelo.setTripulacion(tripulantes);

        // Agregar observaciones si se proporcionan
        if (dto.getObservaciones() != null && !dto.getObservaciones().isBlank()) {
            String obsActuales = vuelo.getObservaciones() != null ? vuelo.getObservaciones() + "\n" : "";
            vuelo.setObservaciones(obsActuales + "[Asignación tripulación] " + dto.getObservaciones());
        }

        vuelo = vueloRepository.save(vuelo);

        // Registrar en historial
        registrarHistorial(vuelo, vuelo.getEstado(), vuelo.getEstado(), "ASIGNACION_TRIPULACION",
                "Tripulación asignada: " + tripulantes.size() + " miembros");

        return vueloMapper.toDTO(vuelo);
    }

    // ==================== HISTORIAL Y CONSULTAS ====================

    @Override
    @Transactional(readOnly = true)
    public List<HistorialVueloDTO> obtenerHistorialVuelo(Long vueloId) {
        // Verificar que el vuelo existe
        if (!vueloRepository.existsById(vueloId)) {
            throw new VueloNoEncontradoException("Vuelo no encontrado con ID: " + vueloId);
        }

        List<HistorialVuelo> historial = historialVueloRepository.findByVueloIdOrderByFechaCambioDesc(vueloId);
        return historialVueloMapper.toDTOList(historial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VueloDTO> obtenerVuelosPorUsuario(Long usuarioId) {
        List<Vuelo> vuelos = vueloRepository.findByUsuarioId(usuarioId);
        return vueloMapper.toDTOList(vuelos);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Registra un cambio en el historial del vuelo.
     */
    private void registrarHistorial(Vuelo vuelo, EstadoVuelo estadoAnterior,
                                    EstadoVuelo estadoNuevo, String tipoAccion, String motivo) {
        HistorialVuelo historial = HistorialVuelo.builder()
                .vuelo(vuelo)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .tipoAccion(tipoAccion)
                .motivo(motivo)
                .fechaCambio(LocalDateTime.now())
                .build();

        historialVueloRepository.save(historial);
    }
}
