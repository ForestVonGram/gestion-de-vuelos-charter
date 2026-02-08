package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.service.base.AeronaveService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final AeronaveMapper aeronaveMapper;

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

        // Validar que el nuevo estado sea diferente
        if (aeronave.getEstado() == nuevoEstado) {
            log.warn("Intento de cambiar a mismo estado. Aeronave ID: {}, Estado actual: {}", id, aeronave.getEstado());
            throw new AeronaveNoDisponibleException("La aeronave ya está en estado " + nuevoEstado);
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
}
