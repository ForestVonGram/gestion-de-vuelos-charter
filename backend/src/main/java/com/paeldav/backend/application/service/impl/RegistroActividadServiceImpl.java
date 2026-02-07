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

    private final RegistroActividadRepository registroActividadRepository;
    private final RegistroActividadMapper registroActividadMapper;

    @Override
    @Transactional
    public RegistroActividadDTO registrarActividad(Long usuarioId, TipoActividad tipoActividad,
                                                    String descripcion, String entidadAfectada,
                                                    String detallesAdicionales) {
        RegistroActividad registro = RegistroActividad.builder()
                .usuarioId(usuarioId)
                .tipoActividad(tipoActividad)
                .descripcion(descripcion)
                .entidadAfectada(entidadAfectada)
                .detallesAdicionales(detallesAdicionales)
                .build();

        registro = registroActividadRepository.save(registro);
        return registroActividadMapper.toDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorUsuario(Long usuarioId) {
        List<RegistroActividad> registros = registroActividadRepository.findByUsuarioId(usuarioId);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorTipo(TipoActividad tipoActividad) {
        List<RegistroActividad> registros = registroActividadRepository.findByTipoActividad(tipoActividad);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        List<RegistroActividad> registros = registroActividadRepository.findByTimestampBetween(inicio, fin);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerActividadesPorUsuarioYFecha(Long usuarioId,
                                                                          LocalDateTime inicio,
                                                                          LocalDateTime fin) {
        List<RegistroActividad> registros = registroActividadRepository
                .findByUsuarioIdAndTimestampBetween(usuarioId, inicio, fin);
        return registroActividadMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroActividadDTO> obtenerTodasLasActividades() {
        List<RegistroActividad> registros = registroActividadRepository.findAll();
        return registroActividadMapper.toDTOList(registros);
    }
}
