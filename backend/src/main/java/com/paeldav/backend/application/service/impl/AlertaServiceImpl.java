package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.alerta.AlertaCreateDTO;
import com.paeldav.backend.application.dto.alerta.AlertaDTO;
import com.paeldav.backend.application.mapper.AlertaMapper;
import com.paeldav.backend.application.service.base.AlertaService;
import com.paeldav.backend.application.service.base.MantenimientoService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Alerta;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.TipoAlerta;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.exception.AlertaNoEncontradaException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.AlertaRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de alertas de mantenimiento.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertaServiceImpl implements AlertaService {

    private final AlertaRepository alertaRepository;
    private final AeronaveRepository aeronaveRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final AlertaMapper alertaMapper;
    private final MantenimientoService mantenimientoService;

    @Override
    public AlertaDTO crearAlerta(AlertaCreateDTO alertaCreateDTO) {
        log.info("Creando nueva alerta tipo {} para aeronave ID: {}", alertaCreateDTO.getTipo(), alertaCreateDTO.getAeronaveId());

        // Validar que la aeronave exista
        Aeronave aeronave = aeronaveRepository.findById(alertaCreateDTO.getAeronaveId())
                .orElseThrow(() -> {
                    log.warn("Intento de crear alerta para aeronave inexistente ID: {}", alertaCreateDTO.getAeronaveId());
                    return new AeronaveNoEncontradaException(
                            "Aeronave no encontrada con ID: " + alertaCreateDTO.getAeronaveId()
                    );
                });

        // Convertir DTO a entidad
        Alerta alerta = alertaMapper.toEntity(alertaCreateDTO);
        alerta.setAeronave(aeronave);
        alerta.setActiva(true);

        // Si se proporcionó ID de mantenimiento relacionado, obtenerlo
        if (alertaCreateDTO.getMantenimientoRelacionadoId() != null) {
            Mantenimiento mantenimiento = mantenimientoRepository.findById(alertaCreateDTO.getMantenimientoRelacionadoId())
                    .orElse(null);
            alerta.setMantenimientoRelacionado(mantenimiento);
        }

        // Guardar en base de datos
        Alerta alertaGuardada = alertaRepository.save(alerta);
        log.info("Alerta creada exitosamente con ID: {} para aeronave: {}", alertaGuardada.getId(), aeronave.getMatricula());

        return alertaMapper.toDTO(alertaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public AlertaDTO obtenerAlertaPorId(Long id) {
        log.debug("Buscando alerta con ID: {}", id);

        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Alerta no encontrada con ID: {}", id);
                    return new AlertaNoEncontradaException("Alerta no encontrada con ID: " + id);
                });

        return alertaMapper.toDTO(alerta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerTodasAlertas() {
        log.debug("Obteniendo todas las alertas");

        List<Alerta> alertas = alertaRepository.findAll();
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasPorAeronave(Long aeronaveId) {
        log.debug("Obteniendo alertas para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener alertas para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Alerta> alertas = alertaRepository.findByAeronaveId(aeronaveId);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasActivasPorAeronave(Long aeronaveId) {
        log.debug("Obteniendo alertas activas para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener alertas activas para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Alerta> alertas = alertaRepository.findAlertasActivasPorAeronave(aeronaveId);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasPorTipo(TipoAlerta tipo) {
        log.debug("Obteniendo alertas de tipo: {}", tipo);

        List<Alerta> alertas = alertaRepository.findByTipo(tipo);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasActivasPorTipo(TipoAlerta tipo) {
        log.debug("Obteniendo alertas activas de tipo: {}", tipo);

        List<Alerta> alertas = alertaRepository.findAlertasActivasPorTipo(tipo);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasActivasPorAeronaveYTipo(Long aeronaveId, TipoAlerta tipo) {
        log.debug("Obteniendo alertas activas tipo {} para aeronave ID: {}", tipo, aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener alertas para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Alerta> alertas = alertaRepository.findAlertasActivasPorAeronaveYTipo(aeronaveId, tipo);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasActivas() {
        log.debug("Obteniendo todas las alertas activas");

        List<Alerta> alertas = alertaRepository.findByActiva(true);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    public AlertaDTO resolverAlerta(Long id, String observaciones) {
        log.info("Resolviendo alerta ID: {}", id);

        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Alerta no encontrada con ID: {}", id);
                    return new AlertaNoEncontradaException("Alerta no encontrada con ID: " + id);
                });

        alerta.setActiva(false);
        alerta.setFechaResolucion(LocalDateTime.now());
        alerta.setObservaciones(observaciones);

        Alerta alertaActualizada = alertaRepository.save(alerta);
        log.info("Alerta resuelta exitosamente con ID: {}", id);

        return alertaMapper.toDTO(alertaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasPorAeronaveYFecha(Long aeronaveId, LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Obteniendo alertas para aeronave ID: {} entre {} y {}", aeronaveId, inicio, fin);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener alertas para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Alerta> alertas = alertaRepository.findAlertasPorAeronaveYFecha(aeronaveId, inicio, fin);
        return alertaMapper.toDTOList(alertas);
    }

    @Override
    public AlertaDTO generarAlertaMantenimientoVencido(Long aeronaveId) {
        log.info("Generando alerta de mantenimiento vencido para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de generar alerta para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Obtener mantenimientos vencidos usando el servicio
        List<Mantenimiento> mantenimientosVencidos = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado() && m.getFechaInicio().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (mantenimientosVencidos.isEmpty()) {
            log.debug("No hay mantenimientos vencidos para aeronave ID: {}", aeronaveId);
            return null;
        }

        // Obtener el mantenimiento más antiguo vencido
        Mantenimiento mantenimientoVencido = mantenimientosVencidos.stream()
                .min((m1, m2) -> m1.getFechaInicio().compareTo(m2.getFechaInicio()))
                .orElse(null);

        if (mantenimientoVencido == null) {
            return null;
        }

        // Crear la alerta
        Aeronave aeronave = aeronaveRepository.findById(aeronaveId).orElseThrow();
        String descripcion = String.format(
                "Mantenimiento %s vencido desde %s para aeronave %s",
                mantenimientoVencido.getTipo(),
                mantenimientoVencido.getFechaInicio(),
                aeronave.getMatricula()
        );

        Alerta alerta = Alerta.builder()
                .aeronave(aeronave)
                .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                .descripcion(descripcion)
                .mantenimientoRelacionado(mantenimientoVencido)
                .activa(true)
                .observaciones("Generada automáticamente por sistema de alertas")
                .build();

        Alerta alertaGuardada = alertaRepository.save(alerta);
        log.info("Alerta de mantenimiento vencido creada con ID: {}", alertaGuardada.getId());

        return alertaMapper.toDTO(alertaGuardada);
    }

    @Override
    public AlertaDTO generarAlertaMantenimientoProximo(Long aeronaveId, int diasAnticipacion) {
        log.info("Generando alerta de mantenimiento próximo para aeronave ID: {} con {} días de anticipación",
                aeronaveId, diasAnticipacion);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de generar alerta para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(diasAnticipacion);

        // Obtener mantenimientos próximos
        List<Mantenimiento> mantenimientosProximos = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado()
                        && m.getFechaInicio().isAfter(ahora)
                        && m.getFechaInicio().isBefore(limite))
                .collect(Collectors.toList());

        if (mantenimientosProximos.isEmpty()) {
            log.debug("No hay mantenimientos próximos para aeronave ID: {}", aeronaveId);
            return null;
        }

        // Obtener el mantenimiento más próximo
        Mantenimiento mantenimientoProximo = mantenimientosProximos.stream()
                .min((m1, m2) -> m1.getFechaInicio().compareTo(m2.getFechaInicio()))
                .orElse(null);

        if (mantenimientoProximo == null) {
            return null;
        }

        // Crear la alerta
        Aeronave aeronave = aeronaveRepository.findById(aeronaveId).orElseThrow();
        String descripcion = String.format(
                "Mantenimiento %s próximo el %s para aeronave %s",
                mantenimientoProximo.getTipo(),
                mantenimientoProximo.getFechaInicio(),
                aeronave.getMatricula()
        );

        Alerta alerta = Alerta.builder()
                .aeronave(aeronave)
                .tipo(TipoAlerta.MANTENIMIENTO_PROXIMO)
                .descripcion(descripcion)
                .mantenimientoRelacionado(mantenimientoProximo)
                .activa(true)
                .observaciones("Generada automáticamente por sistema de alertas")
                .build();

        Alerta alertaGuardada = alertaRepository.save(alerta);
        log.info("Alerta de mantenimiento próximo creada con ID: {}", alertaGuardada.getId());

        return alertaMapper.toDTO(alertaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> obtenerAlertasPorMantenimiento(Long mantenimientoId) {
        log.debug("Obteniendo alertas para mantenimiento ID: {}", mantenimientoId);

        List<Alerta> alertas = alertaRepository.findByMantenimientoRelacionadoId(mantenimientoId);
        return alertaMapper.toDTOList(alertas);
    }
}
