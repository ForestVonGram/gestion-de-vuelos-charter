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
 * Implementación del servicio de validación operativa de aeronaves.
 * Valida si una aeronave puede despegar basado en su estado de mantenimiento.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ValidadorOperativoServiceImpl implements ValidadorOperativoService {

    private final AeronaveRepository aeronaveRepository;
    private final MantenimientoRepository mantenimientoRepository;

    @Override
    public boolean esAeronaveOperativa(Long aeronaveId) {
        log.debug("Validando operatividad de aeronave ID: {}", aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de validar aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Una aeronave NO es operativa si tiene mantenimiento CORRECTIVO o PREVENTIVO vencido
        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);

        boolean tieneMantenimientoVencidoCritico = mantenimientosVencidos.stream()
                .anyMatch(m -> m.getTipo() == TipoMantenimiento.CORRECTIVO || 
                              m.getTipo() == TipoMantenimiento.PREVENTIVO);

        return !tieneMantenimientoVencidoCritico;
    }

    @Override
    public String obtenerRazonNoOperativa(Long aeronaveId) {
        log.debug("Obteniendo razón de no operatividad para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener razón para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);

        if (mantenimientosVencidos.isEmpty()) {
            return null;
        }

        // Obtener el mantenimiento vencido más crítico
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

    @Override
    public boolean tieneMantenimientoVencido(Long aeronaveId) {
        log.debug("Verificando si aeronave ID: {} tiene mantenimiento vencido", aeronaveId);

        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);
        return !mantenimientosVencidos.isEmpty();
    }

    @Override
    public boolean tieneMantenimientoPendiente(Long aeronaveId) {
        log.debug("Verificando si aeronave ID: {} tiene mantenimiento pendiente", aeronaveId);

        List<Mantenimiento> mantenimientosPendientes = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado())
                .collect(Collectors.toList());

        return !mantenimientosPendientes.isEmpty();
    }

    @Override
    public ResumenOperatividad obtenerResumenOperatividad(Long aeronaveId) {
        log.debug("Obteniendo resumen de operatividad para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave exista
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener resumen para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Obtener mantenimientos vencidos
        List<Mantenimiento> mantenimientosVencidos = obtenerMantenimientosVencidos(aeronaveId);

        // Obtener mantenimientos pendientes
        List<Mantenimiento> mantenimientosPendientes = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado())
                .collect(Collectors.toList());

        // Determinar si es operativa
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
     * Método auxiliar para obtener los mantenimientos vencidos de una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de mantenimientos vencidos
     */
    private List<Mantenimiento> obtenerMantenimientosVencidos(Long aeronaveId) {
        LocalDateTime ahora = LocalDateTime.now();

        return mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado() && m.getFechaInicio().isBefore(ahora))
                .collect(Collectors.toList());
    }
}
