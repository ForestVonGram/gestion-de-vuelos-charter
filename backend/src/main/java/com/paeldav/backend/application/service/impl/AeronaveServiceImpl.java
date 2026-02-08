package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.application.dto.aeronave.HistorialUsoAeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.ResumenDisponibilidadFlotaDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.mapper.MantenimientoMapper;
import com.paeldav.backend.application.mapper.RepostajeMapper;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.base.AeronaveService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.entity.Repostaje;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import com.paeldav.backend.infraestructure.repository.RepostajeRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión integral de aeronaves.
 * Maneja registro, actualización, validación de capacidades y búsquedas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AeronaveServiceImpl implements AeronaveService {

    private final AeronaveRepository aeronaveRepository;
    private final VueloRepository vueloRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final RepostajeRepository repostajeRepository;
    private final AeronaveMapper aeronaveMapper;
    private final VueloMapper vueloMapper;
    private final MantenimientoMapper mantenimientoMapper;
    private final RepostajeMapper repostajeMapper;

    @Override
    public AeronaveDTO registrarAeronave(AeronaveCreateDTO aeronaveCreateDTO) {
        log.info("Registrando nueva aeronave con matrícula: {}", aeronaveCreateDTO.getMatricula());

        // Validar que la matrícula no exista
        if (aeronaveRepository.existsByMatricula(aeronaveCreateDTO.getMatricula())) {
            log.warn("Intento de registrar aeronave con matrícula duplicada: {}", aeronaveCreateDTO.getMatricula());
            throw new AeronaveYaExisteException(
                    "Una aeronave con la matrícula " + aeronaveCreateDTO.getMatricula() + " ya existe en el sistema"
            );
        }

        // Convertir DTO a entidad
        Aeronave aeronave = aeronaveMapper.toEntity(aeronaveCreateDTO);

        // Establecer estado por defecto si no se proporciona
        if (aeronave.getEstado() == null) {
            aeronave.setEstado(EstadoAeronave.DISPONIBLE);
        }

        // Guardar en la base de datos
        Aeronave aeronaveGuardada = aeronaveRepository.save(aeronave);
        log.info("Aeronave registrada exitosamente con ID: {}", aeronaveGuardada.getId());

        return aeronaveMapper.toDTO(aeronaveGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public AeronaveDTO obtenerAeronavePorId(Long id) {
        log.debug("Buscando aeronave con ID: {}", id);

        Aeronave aeronave = aeronaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", id);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + id);
                });

        return aeronaveMapper.toDTO(aeronave);
    }

    @Override
    @Transactional(readOnly = true)
    public AeronaveDTO obtenerAeronavePorMatricula(String matricula) {
        log.debug("Buscando aeronave con matrícula: {}", matricula);

        Aeronave aeronave = aeronaveRepository.findByMatricula(matricula)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con matrícula: {}", matricula);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con matrícula: " + matricula);
                });

        return aeronaveMapper.toDTO(aeronave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AeronaveDTO> obtenerTodasAeronaves() {
        log.debug("Obteniendo todas las aeronaves");

        List<Aeronave> aeronaves = aeronaveRepository.findAll();
        return aeronaveMapper.toDTOList(aeronaves);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AeronaveDTO> obtenerAerronavesPorEstado(EstadoAeronave estado) {
        log.debug("Obteniendo aeronaves con estado: {}", estado);

        List<Aeronave> aeronaves = aeronaveRepository.findByEstado(estado);
        return aeronaveMapper.toDTOList(aeronaves);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AeronaveDTO> obtenerAerronavesPorModelo(String modelo) {
        log.debug("Obteniendo aeronaves con modelo: {}", modelo);

        List<Aeronave> aeronaves = aeronaveRepository.findByModelo(modelo);
        return aeronaveMapper.toDTOList(aeronaves);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AeronaveDTO> obtenerAerronavesPorCapacidad(Integer capacidad) {
        log.debug("Obteniendo aeronaves con capacidad >= {}", capacidad);

        List<Aeronave> aeronaves = aeronaveRepository.findByCapacidadPasajerosGreaterThanEqual(capacidad);
        return aeronaveMapper.toDTOList(aeronaves);
    }

    @Override
    public AeronaveDTO actualizarAeronave(Long id, AeronaveUpdateDTO aeronaveUpdateDTO) {
        log.info("Actualizando aeronave con ID: {}", id);

        // Obtener la aeronave existente
        Aeronave aeronave = aeronaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", id);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + id);
                });

        // Aplicar cambios (sin modificar matrícula ni modelo)
        aeronaveMapper.updateEntityFromUpdateDTO(aeronaveUpdateDTO, aeronave);

        // Guardar cambios
        Aeronave aeronaveActualizada = aeronaveRepository.save(aeronave);
        log.info("Aeronave actualizada exitosamente con ID: {}", id);

        return aeronaveMapper.toDTO(aeronaveActualizada);
    }

    @Override
    public AeronaveDTO cambiarEstadoAeronave(Long id, EstadoAeronave nuevoEstado) {
        log.info("Cambiando estado de aeronave ID: {} a: {}", id, nuevoEstado);

        Aeronave aeronave = aeronaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", id);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + id);
                });

        EstadoAeronave estadoActual = aeronave.getEstado();

        // Validar que el nuevo estado sea diferente
        if (estadoActual == nuevoEstado) {
            log.warn("Intento de cambiar a mismo estado. Aeronave ID: {}, Estado actual: {}", id, estadoActual);
            throw new AeronaveNoDisponibleException("La aeronave ya está en estado " + nuevoEstado);
        }

        // Validar transición de estado
        if (!esTransicionEstadoValida(estadoActual, nuevoEstado)) {
            log.warn("Transición de estado no permitida. Aeronave ID: {}, De: {} a: {}", id, estadoActual, nuevoEstado);
            throw new AeronaveNoDisponibleException(
                    String.format("Transición de estado no permitida de %s a %s", estadoActual, nuevoEstado)
            );
        }

        // Cambiar estado
        aeronave.setEstado(nuevoEstado);

        Aeronave aeronaveActualizada = aeronaveRepository.save(aeronave);
        log.info("Estado de aeronave cambiado exitosamente. ID: {}, Nuevo estado: {}", id, nuevoEstado);

        return aeronaveMapper.toDTO(aeronaveActualizada);
    }

    @Override
    public void eliminarAeronave(Long id) {
        log.info("Eliminando aeronave con ID: {}", id);

        Aeronave aeronave = aeronaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", id);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + id);
                });

        // Solo permitir eliminar aeronaves disponibles
        if (aeronave.getEstado() != EstadoAeronave.DISPONIBLE) {
            log.warn("Intento de eliminar aeronave que no está disponible. ID: {}, Estado: {}", id, aeronave.getEstado());
            throw new AeronaveNoDisponibleException(
                    "No se puede eliminar una aeronave que no está en estado DISPONIBLE. Estado actual: " + aeronave.getEstado()
            );
        }

        // Cambiar a estado FUERA_DE_SERVICIO (borrado lógico)
        aeronave.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);
        aeronaveRepository.save(aeronave);

        log.info("Aeronave eliminada (fuera de servicio) con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarCapacidadOperativa(Long aeronaveId, Integer numeroPasajeros, Integer numeroTripulantes) {
        log.debug("Validando capacidad operativa para aeronave ID: {}. Pasajeros: {}, Tripulación: {}",
                aeronaveId, numeroPasajeros, numeroTripulantes);

        Aeronave aeronave = aeronaveRepository.findById(aeronaveId)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada para validar capacidad. ID: {}", aeronaveId);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
                });

        // Validar capacidad de pasajeros
        if (numeroPasajeros > aeronave.getCapacidadPasajeros()) {
            log.warn("Capacidad de pasajeros insuficiente. Requeridos: {}, Disponibles: {}, Aeronave ID: {}",
                    numeroPasajeros, aeronave.getCapacidadPasajeros(), aeronaveId);
            throw new CapacidadInsuficienteException(
                    String.format("Capacidad de pasajeros insuficiente. La aeronave puede transportar máximo %d pasajeros pero se requieren %d",
                            aeronave.getCapacidadPasajeros(), numeroPasajeros)
            );
        }

        // Validar capacidad de tripulación
        if (numeroTripulantes > aeronave.getCapacidadTripulacion()) {
            log.warn("Capacidad de tripulación insuficiente. Requeridos: {}, Disponibles: {}, Aeronave ID: {}",
                    numeroTripulantes, aeronave.getCapacidadTripulacion(), aeronaveId);
            throw new CapacidadInsuficienteException(
                    String.format("Capacidad de tripulación insuficiente. La aeronave puede tener máximo %d tripulantes pero se requieren %d",
                            aeronave.getCapacidadTripulacion(), numeroTripulantes)
            );
        }

        log.debug("Validación de capacidad exitosa para aeronave ID: {}", aeronaveId);
    }

    @Override
    public void incrementarHorasVuelo(Long aeronaveId, Double horasVuelo) {
        log.info("Incrementando horas de vuelo para aeronave ID: {}. Horas a agregar: {}", aeronaveId, horasVuelo);

        Aeronave aeronave = aeronaveRepository.findById(aeronaveId)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada para incrementar horas. ID: {}", aeronaveId);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
                });

        // Validar que las horas a agregar sean positivas
        if (horasVuelo == null || horasVuelo <= 0) {
            log.warn("Intento de agregar horas de vuelo inválidas. Aeronave ID: {}, Horas: {}", aeronaveId, horasVuelo);
            throw new IllegalArgumentException("Las horas de vuelo a agregar deben ser positivas");
        }

        // Incrementar horas
        Double horasActuales = aeronave.getHorasVueloTotales() != null ? aeronave.getHorasVueloTotales() : 0.0;
        aeronave.setHorasVueloTotales(horasActuales + horasVuelo);

        aeronaveRepository.save(aeronave);
        log.info("Horas de vuelo incrementadas exitosamente. Aeronave ID: {}, Nuevas horas totales: {}",
                aeronaveId, aeronave.getHorasVueloTotales());
    }

    @Override
    public AeronaveDTO bloquearAeronave(Long id, String motivo) {
        log.info("Bloqueando aeronave con ID: {}. Motivo: {}", id, motivo);

        Aeronave aeronave = aeronaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", id);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + id);
                });

        // No permitir bloquear aeronaves en vuelo
        if (aeronave.getEstado() == EstadoAeronave.EN_VUELO) {
            log.warn("Intento de bloquear aeronave en vuelo. ID: {}", id);
            throw new AeronaveNoDisponibleException(
                    "No se puede bloquear una aeronave que está EN_VUELO. Espere a que aterrice."
            );
        }

        // Si ya está fuera de servicio, no hacer nada
        if (aeronave.getEstado() == EstadoAeronave.FUERA_DE_SERVICIO) {
            log.info("La aeronave ya está bloqueada (FUERA_DE_SERVICIO). ID: {}", id);
            return aeronaveMapper.toDTO(aeronave);
        }

        // Cambiar a estado FUERA_DE_SERVICIO
        aeronave.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);

        Aeronave aeronaveBloqueada = aeronaveRepository.save(aeronave);
        log.info("Aeronave bloqueada exitosamente. ID: {}, Motivo: {}", id, motivo);

        return aeronaveMapper.toDTO(aeronaveBloqueada);
    }

    @Override
    public AeronaveDTO desbloquearAeronave(Long id) {
        log.info("Desbloqueando aeronave con ID: {}", id);

        Aeronave aeronave = aeronaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", id);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + id);
                });

        // Solo se pueden desbloquear aeronaves que estén FUERA_DE_SERVICIO
        if (aeronave.getEstado() != EstadoAeronave.FUERA_DE_SERVICIO) {
            log.warn("Intento de desbloquear aeronave que no está bloqueada. ID: {}, Estado: {}", id, aeronave.getEstado());
            throw new AeronaveNoDisponibleException(
                    "Solo se pueden desbloquear aeronaves en estado FUERA_DE_SERVICIO. Estado actual: " + aeronave.getEstado()
            );
        }

        // Cambiar a estado DISPONIBLE
        aeronave.setEstado(EstadoAeronave.DISPONIBLE);

        Aeronave aeronaveDesbloqueada = aeronaveRepository.save(aeronave);
        log.info("Aeronave desbloqueada exitosamente. ID: {}", id);

        return aeronaveMapper.toDTO(aeronaveDesbloqueada);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDisponibilidadFlotaDTO consultarResumenDisponibilidadFlota() {
        log.debug("Consultando resumen de disponibilidad de flota");

        List<Aeronave> todasAeronaves = aeronaveRepository.findAll();

        // Contar por estado
        Map<String, Integer> contadorPorEstado = new HashMap<>();
        int disponibles = 0;
        int enVuelo = 0;
        int enMantenimiento = 0;
        int fueraDeServicio = 0;

        for (Aeronave aeronave : todasAeronaves) {
            String estado = aeronave.getEstado().name();
            contadorPorEstado.merge(estado, 1, Integer::sum);

            switch (aeronave.getEstado()) {
                case DISPONIBLE -> disponibles++;
                case EN_VUELO -> enVuelo++;
                case EN_MANTENIMIENTO -> enMantenimiento++;
                case FUERA_DE_SERVICIO -> fueraDeServicio++;
            }
        }

        // Obtener listas de aeronaves disponibles y bloqueadas
        List<AeronaveDTO> aeronavesDisponiblesList = aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE)
                .stream()
                .map(aeronaveMapper::toDTO)
                .collect(Collectors.toList());

        List<AeronaveDTO> aeronavesBloqueadasList = aeronaveRepository.findByEstado(EstadoAeronave.FUERA_DE_SERVICIO)
                .stream()
                .map(aeronaveMapper::toDTO)
                .collect(Collectors.toList());

        // Calcular porcentaje de disponibilidad
        int total = todasAeronaves.size();
        double porcentajeDisponibilidad = total > 0 ? (disponibles * 100.0) / total : 0.0;

        return ResumenDisponibilidadFlotaDTO.builder()
                .totalAeronaves(total)
                .aeronavesDisponibles(disponibles)
                .aeronavesEnVuelo(enVuelo)
                .aeronavesEnMantenimiento(enMantenimiento)
                .aeronavesFueraDeServicio(fueraDeServicio)
                .contadorPorEstado(contadorPorEstado)
                .listaAeronavesDisponibles(aeronavesDisponiblesList)
                .listaAerronavesBloqueadas(aeronavesBloqueadasList)
                .fechaConsulta(LocalDateTime.now())
                .porcentajeDisponibilidad(Math.round(porcentajeDisponibilidad * 100.0) / 100.0)
                .build();
    }

    @Override
    public boolean esTransicionEstadoValida(EstadoAeronave estadoActual, EstadoAeronave nuevoEstado) {
        // Definir transiciones permitidas
        // DISPONIBLE -> EN_VUELO, EN_MANTENIMIENTO, FUERA_DE_SERVICIO
        // EN_VUELO -> DISPONIBLE (aterriza), FUERA_DE_SERVICIO (emergencia)
        // EN_MANTENIMIENTO -> DISPONIBLE, FUERA_DE_SERVICIO
        // FUERA_DE_SERVICIO -> DISPONIBLE, EN_MANTENIMIENTO

        return switch (estadoActual) {
            case DISPONIBLE -> nuevoEstado == EstadoAeronave.EN_VUELO ||
                               nuevoEstado == EstadoAeronave.EN_MANTENIMIENTO ||
                               nuevoEstado == EstadoAeronave.FUERA_DE_SERVICIO;
            case EN_VUELO -> nuevoEstado == EstadoAeronave.DISPONIBLE ||
                             nuevoEstado == EstadoAeronave.FUERA_DE_SERVICIO;
            case EN_MANTENIMIENTO -> nuevoEstado == EstadoAeronave.DISPONIBLE ||
                                     nuevoEstado == EstadoAeronave.FUERA_DE_SERVICIO;
            case FUERA_DE_SERVICIO -> nuevoEstado == EstadoAeronave.DISPONIBLE ||
                                      nuevoEstado == EstadoAeronave.EN_MANTENIMIENTO;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialUsoAeronaveDTO obtenerHistorialUso(Long aeronaveId) {
        log.info("Obteniendo historial de uso completo para aeronave ID: {}", aeronaveId);
        return obtenerHistorialUsoInterno(aeronaveId, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialUsoAeronaveDTO obtenerHistorialUso(Long aeronaveId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        log.info("Obteniendo historial de uso para aeronave ID: {} desde {} hasta {}", aeronaveId, fechaDesde, fechaHasta);
        return obtenerHistorialUsoInterno(aeronaveId, fechaDesde, fechaHasta);
    }

    /**
     * Método interno que construye el historial de uso de una aeronave.
     */
    private HistorialUsoAeronaveDTO obtenerHistorialUsoInterno(Long aeronaveId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        // Obtener la aeronave
        Aeronave aeronave = aeronaveRepository.findById(aeronaveId)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
                });

        AeronaveDTO aeronaveDTO = aeronaveMapper.toDTO(aeronave);

        // Obtener vuelos
        List<Vuelo> vuelos = vueloRepository.findByAeronaveId(aeronaveId);
        if (fechaDesde != null && fechaHasta != null) {
            vuelos = vuelos.stream()
                    .filter(v -> v.getFechaSalidaProgramada() != null &&
                            !v.getFechaSalidaProgramada().isBefore(fechaDesde) &&
                            !v.getFechaSalidaProgramada().isAfter(fechaHasta))
                    .collect(Collectors.toList());
        }
        List<VueloDTO> vuelosDTO = vueloMapper.toDTOList(vuelos);

        // Obtener mantenimientos
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByAeronaveId(aeronaveId);
        if (fechaDesde != null && fechaHasta != null) {
            mantenimientos = mantenimientos.stream()
                    .filter(m -> m.getFechaInicio() != null &&
                            !m.getFechaInicio().isBefore(fechaDesde) &&
                            !m.getFechaInicio().isAfter(fechaHasta))
                    .collect(Collectors.toList());
        }
        List<MantenimientoDTO> mantenimientosDTO = mantenimientoMapper.toDTOList(mantenimientos);

        // Obtener repostajes
        List<Repostaje> repostajes = repostajeRepository.findByAeronaveId(aeronaveId);
        if (fechaDesde != null && fechaHasta != null) {
            repostajes = repostajes.stream()
                    .filter(r -> r.getFechaRepostaje() != null &&
                            !r.getFechaRepostaje().isBefore(fechaDesde) &&
                            !r.getFechaRepostaje().isAfter(fechaHasta))
                    .collect(Collectors.toList());
        }
        List<RepostajeDTO> repostajesDTO = repostajeMapper.toDTOList(repostajes);

        // Calcular estadísticas de vuelos
        int totalVuelos = vuelos.size();
        int vuelosCompletados = (int) vuelos.stream()
                .filter(v -> v.getEstado() == EstadoVuelo.COMPLETADO)
                .count();
        int vuelosCancelados = (int) vuelos.stream()
                .filter(v -> v.getEstado() == EstadoVuelo.CANCELADO)
                .count();

        // Calcular horas de vuelo (diferencia entre salida y llegada real)
        double totalHorasVuelo = vuelos.stream()
                .filter(v -> v.getFechaSalidaReal() != null && v.getFechaLlegadaReal() != null)
                .mapToDouble(v -> {
                    long minutos = java.time.Duration.between(v.getFechaSalidaReal(), v.getFechaLlegadaReal()).toMinutes();
                    return minutos / 60.0;
                })
                .sum();

        // Calcular estadísticas de mantenimientos
        int totalMantenimientos = mantenimientos.size();
        int mantenimientosPreventivos = (int) mantenimientos.stream()
                .filter(m -> m.getTipo() == TipoMantenimiento.PREVENTIVO)
                .count();
        int mantenimientosCorrectivos = (int) mantenimientos.stream()
                .filter(m -> m.getTipo() == TipoMantenimiento.CORRECTIVO)
                .count();
        double costoTotalMantenimientos = mantenimientos.stream()
                .filter(m -> m.getCosto() != null)
                .mapToDouble(Mantenimiento::getCosto)
                .sum();

        // Calcular estadísticas de repostajes
        int totalRepostajes = repostajes.size();
        double totalLitrosCombustible = repostajes.stream()
                .filter(r -> r.getCantidadLitros() != null)
                .mapToDouble(Repostaje::getCantidadLitros)
                .sum();
        double costoTotalCombustible = repostajes.stream()
                .filter(r -> r.getCostoTotal() != null)
                .mapToDouble(Repostaje::getCostoTotal)
                .sum();

        log.info("Historial de uso generado para aeronave ID: {}. Vuelos: {}, Mantenimientos: {}, Repostajes: {}",
                aeronaveId, totalVuelos, totalMantenimientos, totalRepostajes);

        return HistorialUsoAeronaveDTO.builder()
                .aeronave(aeronaveDTO)
                .vuelos(vuelosDTO)
                .mantenimientos(mantenimientosDTO)
                .repostajes(repostajesDTO)
                .totalVuelos(totalVuelos)
                .vuelosCompletados(vuelosCompletados)
                .vuelosCancelados(vuelosCancelados)
                .totalHorasVuelo(Math.round(totalHorasVuelo * 100.0) / 100.0)
                .totalMantenimientos(totalMantenimientos)
                .mantenimientosPreventivos(mantenimientosPreventivos)
                .mantenimientosCorrectivos(mantenimientosCorrectivos)
                .costoTotalMantenimientos(Math.round(costoTotalMantenimientos * 100.0) / 100.0)
                .totalRepostajes(totalRepostajes)
                .totalLitrosCombustible(Math.round(totalLitrosCombustible * 100.0) / 100.0)
                .costoTotalCombustible(Math.round(costoTotalCombustible * 100.0) / 100.0)
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .fechaGeneracion(LocalDateTime.now())
                .build();
    }
}
