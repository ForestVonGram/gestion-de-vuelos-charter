package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.registroactividad.RegistroActividadDTO;
import com.paeldav.backend.application.mapper.RegistroActividadMapper;
import com.paeldav.backend.application.service.base.RegistroActividadService;
import com.paeldav.backend.domain.entity.RegistroActividad;
import com.paeldav.backend.domain.enums.TipoActividad;
import com.paeldav.backend.infraestructure.repository.RegistroActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de gestión de registros de actividad.
 */
@Service
@RequiredArgsConstructor
public class RegistroActividadServiceImpl implements RegistroActividadService {

    // Dependencias inyectadas automáticamente para el acceso a datos y mapeo
    private final RegistroActividadRepository registroActividadRepository;
    private final RegistroActividadMapper registroActividadMapper;

    @Override
    @Transactional
    public RegistroActividadDTO registrarActividad(Long usuarioId, TipoActividad tipoActividad,
                                                   String descripcion, String entidadAfectada,
                                                   String detallesAdicionales) {
        // Construir la entidad de registro utilizando el patrón Builder con los datos proporcionados
        RegistroActividad registro = RegistroActividad.builder()
                .usuarioId(usuarioId)
                .tipoActividad(tipoActividad)
                .descripcion(descripcion)
                .entidadAfectada(entidadAfectada)
                .detallesAdicionales(detallesAdicionales)
                .build();

        // Persistir el nuevo registro de actividad en la base de datos
        registro = registroActividadRepository.save(registro);

        // Mapear la entidad guardada a DTO para retornarla al cliente
        return registroActividadMapper.toDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorUsuario(Long usuarioId) {
        // Buscar y retornar el historial completo de actividades de un usuario específico
        List<RegistroActividad> registros = registroActividadRepository.findByUsuarioId(usuarioId);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorTipo(TipoActividad tipoActividad) {
        // Filtrar y devolver todos los registros que coincidan con un tipo de actividad particular
        List<RegistroActividad> registros = registroActividadRepository.findByTipoActividad(tipoActividad);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        // Consultar las actividades registradas globalmente dentro de un rango de tiempo definido
        List<RegistroActividad> registros = registroActividadRepository.findByTimestampBetween(inicio, fin);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorUsuarioYFecha(Long usuarioId,
                                                                         LocalDateTime inicio,
                                                                         LocalDateTime fin) {
        // Obtener las actividades de un usuario en particular, limitadas a un periodo de tiempo exacto
        List<RegistroActividad> registros = registroActividadRepository
                .findByUsuarioIdAndTimestampBetween(usuarioId, inicio, fin);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerTodasLasActividades() {
        // Recuperar el listado completo de todas las actividades registradas en el sistema
        List<RegistroActividad> registros = registroActividadRepository.findAll();
        return registroActividadMapper.toDTOList(registros);
    }
}