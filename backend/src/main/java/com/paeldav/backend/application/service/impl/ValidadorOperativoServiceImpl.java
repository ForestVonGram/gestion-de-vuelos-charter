package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.service.base.ValidadorOperativoService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de validar si una aeronave puede operar
 * según su estado de mantenimiento.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ValidadorOperativoServiceImpl implements ValidadorOperativoService {

    private final AeronaveRepository aeronaveRepository;
    private final MantenimientoRepository mantenimientoRepository;

    /**
     * Verifica si una aeronave puede operar.
     */
    @Override
    public boolean esAeronaveOperativa(Long aeronaveId) {
        log.debug("Validando operatividad de aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de validar aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);

        boolean tieneMantenimientoVencidoCritico = mantenimientosVencidos.stream()
                .anyMatch(m -> m.getTipo() == TipoMantenimiento.CORRECTIVO ||
                        m.getTipo() == TipoMantenimiento.PREVENTIVO);

        return !tieneMantenimientoVencidoCritico;
    }

    /**
     * Obtiene la razón por la cual una aeronave no es operativa.
     */
    @Override
    public String obtenerRazonNoOperativa(Long aeronaveId) {
        log.debug("Obteniendo razón de no operatividad para aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener razón para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);

        if (mantenimientosVencidos.isEmpty()) {
            return null;
        }

        Mantenimiento mantenimientoVencido = mantenimientosVencidos.stream()
                .filter(m -> m.getTipo() == TipoMantenimiento.CORRECTIVO)
                .findFirst()
                .orElseGet(() -> mantenimientosVencidos.get(0));

        return String.format(
                "Aeronave bloqueada: Mantenimiento %s pendiente desde %s. Tipo: %s",
                mantenimientoVencido.getId(),
                mantenimientoVencido.getFechaInicio(),
                mantenimientoVencido.getTipo()
        );
    }

    /**
     * Verifica si la aeronave tiene mantenimientos vencidos.
     */
    @Override
    public boolean tieneMantenimientoVencido(Long aeronaveId) {
        log.debug("Verificando si aeronave ID: {} tiene mantenimiento vencido", aeronaveId);

        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);
        return !mantenimientosVencidos.isEmpty();
    }

    /**
     * Verifica si la aeronave tiene mantenimientos pendientes.
     */
    @Override
    public boolean tieneMantenimientoPendiente(Long aeronaveId) {
        log.debug("Verificando si aeronave ID: {} tiene mantenimiento pendiente", aeronaveId);

        List<Mantenimiento> mantenimientosPendientes = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado())
                .collect(Collectors.toList());

        return !mantenimientosPendientes.isEmpty();
    }

    /**
     * Obtiene un resumen del estado operativo de la aeronave.
     */
    @Override
    public ResumenOperatividad obtenerResumenOperatividad(Long aeronaveId) {
        log.debug("Obteniendo resumen de operatividad para aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener resumen para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);

        List<Mantenimiento> mantenimientosPendientes = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado())
                .collect(Collectors.toList());

        boolean esOperativa = esAeronaveOperativa(aeronaveId);
        String razon = obtenerRazonNoOperativa(aeronaveId);

        int cantidadVencidos = mantenimientosVencidos.size();
        int cantidadPendientes = mantenimientosPendientes.size();

        return new ResumenOperatividad(
                esOperativa,
                razon,
                !mantenimientosVencidos.isEmpty(),
                !mantenimientosPendientes.isEmpty(),
                cantidadVencidos,
                cantidadPendientes
        );
    }

    /**
     * Obtiene los mantenimientos vencidos de una aeronave.
     */
    private List<Mantenimiento> obtenerMantenimientosVencidos(Long aeronaveId) {
        LocalDateTime ahora = LocalDateTime.now();

        return mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado() && m.getFechaInicio().isBefore(ahora))
                .collect(Collectors.toList());
    }
}