package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoCreateDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.mapper.MantenimientoMapper;
import com.paeldav.backend.application.service.base.MantenimientoService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.exception.MantenimientoNoEncontradoException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de mantenimiento de aeronaves.
 * Maneja el registro y seguimiento de mantenimientos preventivos, correctivos,
 * repostajes e inspecciones técnicas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MantenimientoServiceImpl implements MantenimientoService {

    // Repositorios y mapper inyectados automáticamente
    private final MantenimientoRepository mantenimientoRepository;
    private final AeronaveRepository aeronaveRepository;
    private final UsuarioRepository usuarioRepository;
    private final MantenimientoMapper mantenimientoMapper;

    @Override
    public MantenimientoDTO registrarMantenimiento(MantenimientoCreateDTO mantenimientoCreateDTO) {
        log.info("Registrando nuevo mantenimiento tipo {} para aeronave ID: {}",
                mantenimientoCreateDTO.getTipo(), mantenimientoCreateDTO.getAeronaveId());

        // Validar que la aeronave objetivo exista en la base de datos
        Aeronave aeronave = aeronaveRepository.findById(mantenimientoCreateDTO.getAeronaveId())
                .orElseThrow(() -> {
                    log.warn("Intento de registrar mantenimiento para aeronave inexistente ID: {}",
                            mantenimientoCreateDTO.getAeronaveId());
                    return new AeronaveNoEncontradaException(
                            "Aeronave no encontrada con ID: " + mantenimientoCreateDTO.getAeronaveId()
                    );
                });

        // Buscar y asignar el usuario responsable, si se especificó uno
        Usuario responsable = null;
        if (mantenimientoCreateDTO.getResponsableId() != null) {
            responsable = usuarioRepository.findById(mantenimientoCreateDTO.getResponsableId())
                    .orElseThrow(() -> {
                        log.warn("Usuario responsable no encontrado con ID: {}",
                                mantenimientoCreateDTO.getResponsableId());
                        return new UsuarioNoEncontradoException(
                                "Usuario no encontrado con ID: " + mantenimientoCreateDTO.getResponsableId()
                        );
                    });
        }

        // Mapear el DTO a la entidad y establecer los valores iniciales
        Mantenimiento mantenimiento = mantenimientoMapper.toEntity(mantenimientoCreateDTO);
        mantenimiento.setAeronave(aeronave);
        mantenimiento.setResponsable(responsable);
        mantenimiento.setCompletado(false);

        // Asignar la fecha actual si no se proporcionó una fecha de inicio
        if (mantenimiento.getFechaInicio() == null) {
            mantenimiento.setFechaInicio(LocalDateTime.now());
        }

        // Persistir el registro y devolver el DTO resultante
        Mantenimiento mantenimientoGuardado = mantenimientoRepository.save(mantenimiento);
        log.info("Mantenimiento registrado exitosamente con ID: {} para aeronave: {}",
                mantenimientoGuardado.getId(), aeronave.getMatricula());

        return mantenimientoMapper.toDTO(mantenimientoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public MantenimientoDTO obtenerMantenimientoPorId(Long id) {
        log.debug("Buscando mantenimiento con ID: {}", id);

        // Buscar el mantenimiento específico o lanzar excepción si no existe
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Mantenimiento no encontrado con ID: {}", id);
                    return new MantenimientoNoEncontradoException("Mantenimiento no encontrado con ID: " + id);
                });

        return mantenimientoMapper.toDTO(mantenimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerTodosMantenimientos() {
        log.debug("Obteniendo todos los mantenimientos");

        // Recuperar y retornar el historial completo de mantenimientos
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll();
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPorAeronave(Long aeronaveId) {
        log.debug("Obteniendo mantenimientos para aeronave ID: {}", aeronaveId);

        // Comprobar la existencia de la aeronave antes de realizar la búsqueda
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener mantenimientos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Retornar la lista de mantenimientos asociados a dicha aeronave
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByAeronaveId(aeronaveId);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPendientesPorAeronave(Long aeronaveId) {
        log.debug("Obteniendo mantenimientos pendientes para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener mantenimientos pendientes para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Obtener los mantenimientos de la aeronave y filtrar en memoria los no completados
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado())
                .collect(Collectors.toList());

        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPorTipo(TipoMantenimiento tipo) {
        log.debug("Obteniendo mantenimientos de tipo: {}", tipo);

        // Buscar mantenimientos basándose en su clasificación (preventivo, correctivo, etc.)
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByTipo(tipo);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPorAeronaveYTipo(Long aeronaveId, TipoMantenimiento tipo) {
        log.debug("Obteniendo mantenimientos tipo {} para aeronave ID: {}", tipo, aeronaveId);

        // Validar existencia de la aeronave antes de la consulta
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener mantenimientos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Retornar mantenimientos de una aeronave concreta filtrados por tipo
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByAeronaveIdAndTipo(aeronaveId, tipo);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Obteniendo mantenimientos entre {} y {}", inicio, fin);

        // Buscar mantenimientos programados o iniciados en un rango de fechas
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByFechaInicioBetween(inicio, fin);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    public MantenimientoDTO completarMantenimiento(Long id, LocalDateTime fechaFin, String observaciones) {
        log.info("Completando mantenimiento ID: {}", id);

        // Verificar que el registro de mantenimiento exista
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Mantenimiento no encontrado con ID: {}", id);
                    return new MantenimientoNoEncontradoException("Mantenimiento no encontrado con ID: " + id);
                });

        // Actualizar el estado indicando fecha de cierre, observaciones y marcado como completado
        mantenimiento.setFechaFin(fechaFin);
        mantenimiento.setObservaciones(observaciones);
        mantenimiento.setCompletado(true);

        // Guardar la actualización
        Mantenimiento mantenimientoActualizado = mantenimientoRepository.save(mantenimiento);
        log.info("Mantenimiento completado exitosamente con ID: {}", id);

        return mantenimientoMapper.toDTO(mantenimientoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerUltimosMantenimientos(Long aeronaveId) {
        log.debug("Obteniendo últimos mantenimientos para aeronave ID: {}", aeronaveId);

        // Comprobar que la aeronave es válida
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener últimos mantenimientos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Consultar los registros más recientes para la aeronave dada
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findUltimosMantenimientos(aeronaveId);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPendientes() {
        log.debug("Obteniendo todos los mantenimientos pendientes");

        // Traer globalmente todos los mantenimientos que aún no se han cerrado
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByCompletado(false);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosPorResponsable(Long responsableId) {
        log.debug("Obteniendo mantenimientos asignados al responsable ID: {}", responsableId);

        // Validar que el usuario responsable exista
        if (!usuarioRepository.existsById(responsableId)) {
            log.warn("Usuario responsable no encontrado con ID: {}", responsableId);
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + responsableId);
        }

        // Devolver la carga de trabajo (mantenimientos) de un empleado específico
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findByResponsableId(responsableId);
        return mantenimientoMapper.toDTOList(mantenimientos);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarMantenimientoVencido(Long aeronaveId) {
        log.debug("Verificando mantenimiento vencido para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de verificar mantenimiento para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Comprobar si hay algún mantenimiento no completado cuya fecha de inicio ya pasó
        LocalDateTime ahora = LocalDateTime.now();
        return mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .anyMatch(m -> !m.getCompletado() && m.getFechaInicio().isBefore(ahora));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO> obtenerMantenimientosVencidos(Long aeronaveId) {
        log.debug("Obteniendo mantenimientos vencidos para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener mantenimientos vencidos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Filtrar y retornar aquellos mantenimientos incompletos con fecha programada en el pasado
        LocalDateTime ahora = LocalDateTime.now();
        List<Mantenimiento> mantenimientosVencidos = mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .filter(m -> !m.getCompletado() && m.getFechaInicio().isBefore(ahora))
                .collect(Collectors.toList());

        return mantenimientoMapper.toDTOList(mantenimientosVencidos);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarMantenimientoPendiente(Long aeronaveId) {
        log.debug("Verificando mantenimiento pendiente para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de verificar mantenimiento para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Retornar verdadero si existe al menos un mantenimiento sin completar para la aeronave
        return mantenimientoRepository.findByAeronaveId(aeronaveId)
                .stream()
                .anyMatch(m -> !m.getCompletado());
    }
}