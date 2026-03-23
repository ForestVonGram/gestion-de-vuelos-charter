package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.reporte.ReporteCreateDTO;
import com.paeldav.backend.application.dto.reporte.ReporteFiltroDTO;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.base.ReporteGeneralService;
import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.TipoReporte;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.ReporteRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar los reportes del sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReporteGeneralServiceImpl implements ReporteGeneralService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteMapper reporteMapper;

    /**
     * Genera un nuevo reporte operativo.
     */
    @Override
    public ReporteDTO generarReporteOperativo(ReporteCreateDTO createDTO, Long usuarioIdAutenticado) {
        log.info("Generando reporte operativo tipo {} para usuario {}", createDTO.getTipo(), usuarioIdAutenticado);

        // Validar rango de fechas
        if (!validarRangoFechas(createDTO.getFechaInicioRango().toLocalDate().toEpochDay(),
                createDTO.getFechaFinRango().toLocalDate().toEpochDay())) {
            throw new IllegalArgumentException("El rango de fechas no es válido");
        }

        // Obtener usuario autenticado
        Usuario usuario = usuarioRepository.findById(usuarioIdAutenticado)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        // Crear entidad Reporte
        Reporte reporte = Reporte.builder()
                .tipo(createDTO.getTipo())
                .descripcion(createDTO.getDescripcion() != null ?
                        createDTO.getDescripcion() : "Reporte " + createDTO.getTipo())
                .fechaInicioRango(createDTO.getFechaInicioRango())
                .fechaFinRango(createDTO.getFechaFinRango())
                .generadoPor(usuario)
                .observaciones(createDTO.getObservaciones())
                .numeroRegistros(0)
                .build();

        // Guardar en base de datos
        Reporte reporteGuardado = reporteRepository.save(reporte);
        log.info("Reporte operativo creado con ID {}", reporteGuardado.getId());

        return reporteMapper.toDTO(reporteGuardado);
    }

    /**
     * Obtiene un reporte por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public ReporteDTO obtenerReportePorId(Long id) {
        log.info("Obteniendo reporte con ID {}", id);
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado con ID: " + id));
        return reporteMapper.toDTO(reporte);
    }

    /**
     * Lista reportes aplicando filtros opcionales.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> listarReportes(ReporteFiltroDTO filtro) {
        log.info("Listando reportes con filtros: tipo={}, fechaDesde={}, fechaHasta={}",
                filtro.getTipo(), filtro.getFechaDesde(), filtro.getFechaHasta());

        List<Reporte> reportes;

        if (filtro.getTipo() != null && filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            reportes = reporteRepository.findByTipoAndFechaGeneracionBetween(
                    filtro.getTipo(), filtro.getFechaDesde(), filtro.getFechaHasta());
        } else if (filtro.getTipo() != null) {
            reportes = reporteRepository.findByTipo(filtro.getTipo());
        } else if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            reportes = reporteRepository.findByFechaGeneracionBetween(
                    filtro.getFechaDesde(), filtro.getFechaHasta());
        } else {
            reportes = reporteRepository.findAll();
        }

        return reportes.stream()
                .map(reporteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los reportes registrados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerTodosReportes() {
        log.info("Obteniendo todos los reportes");
        return reporteRepository.findAll().stream()
                .map(reporteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un reporte por su ID.
     */
    @Override
    public void eliminarReporte(Long id) {
        log.info("Eliminando reporte con ID {}", id);
        if (!reporteRepository.existsById(id)) {
            throw new IllegalArgumentException("Reporte no encontrado con ID: " + id);
        }
        reporteRepository.deleteById(id);
        log.info("Reporte eliminado exitosamente");
    }

    /**
     * Valida que el rango de fechas sea correcto.
     */
    @Override
    public boolean validarRangoFechas(Long fechaInicio, Long fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }
        return fechaInicio < fechaFin;
    }
}