package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.registroauditoria.RegistroAuditoriaDTO;
import com.paeldav.backend.application.mapper.RegistroAuditoriaMapper;
import com.paeldav.backend.application.service.base.RegistroAuditoriaService;
import com.paeldav.backend.domain.entity.RegistroAuditoria;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import com.paeldav.backend.infraestructure.repository.RegistroAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de gestión de registros de auditoría.
 */
@Service
@RequiredArgsConstructor
public class RegistroAuditoriaServiceImpl implements RegistroAuditoriaService {

    private final RegistroAuditoriaRepository registroAuditoriaRepository;
    private final RegistroAuditoriaMapper registroAuditoriaMapper;

    @Override
    @Transactional
    public RegistroAuditoriaDTO registrarEvento(Long usuarioId, TipoEventoAuditoria tipoEvento,
                                               String directorIP, String navegador,
                                               Boolean resultado, String detallesError) {
        RegistroAuditoria registro = RegistroAuditoria.builder()
                .usuarioId(usuarioId)
                .tipoEvento(tipoEvento)
                .directorIP(directorIP)
                .navegador(navegador)
                .resultado(resultado)
                .detallesError(detallesError)
                .build();

        registro = registroAuditoriaRepository.save(registro);
        return registroAuditoriaMapper.toDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerAuditoriaPorUsuario(Long usuarioId) {
        List<RegistroAuditoria> registros = registroAuditoriaRepository.findByUsuarioId(usuarioId);
        return registroAuditoriaMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerEventosPorTipo(TipoEventoAuditoria tipoEvento) {
        List<RegistroAuditoria> registros = registroAuditoriaRepository.findByTipoEvento(tipoEvento);
        return registroAuditoriaMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerEventosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        List<RegistroAuditoria> registros = registroAuditoriaRepository.findByTimestampBetween(inicio, fin);
        return registroAuditoriaMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerEventosPorUsuarioYFecha(Long usuarioId,
                                                                      LocalDateTime inicio,
                                                                      LocalDateTime fin) {
        List<RegistroAuditoria> registros = registroAuditoriaRepository
                .findByUsuarioIdAndTimestampBetween(usuarioId, inicio, fin);
        return registroAuditoriaMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerIntentosAccesoDenegados() {
        List<RegistroAuditoria> registros = registroAuditoriaRepository.findByResultadoFalse();
        return registroAuditoriaMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerIntentosAccesoDenegadosPorTipo(TipoEventoAuditoria tipoEvento) {
        List<RegistroAuditoria> registros = registroAuditoriaRepository
                .findByTipoEventoAndResultadoFalse(tipoEvento);
        return registroAuditoriaMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> obtenerTodosLosEventos() {
        List<RegistroAuditoria> registros = registroAuditoriaRepository.findAll();
        return registroAuditoriaMapper.toDTOList(registros);
    }
}
