package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.base.ReporteFlotaService;
import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoReporte;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de reportes de uso de flota.
 * Genera reportes sobre utilización, mantenimiento y disponibilidad de aeronaves.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReporteFlotaServiceImpl implements ReporteFlotaService {

    private final ReporteRepository reporteRepository;
    private final AeronaveRepository aeronaveRepository;
    private final VueloRepository vueloRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final RepostajeRepository repostajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteMapper reporteMapper;

    @Override
    public ReporteDTO generarReporteUsoFlota(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long usuarioIdAutenticado) {
        log.info("Generando reporte de uso de flota desde {} hasta {}", fechaInicio, fechaFin);

        Usuario usuario = usuarioRepository.findById(usuarioIdAutenticado)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        // Calcular estadísticas
        Map<String, Object> estadisticas = calcularEstadisticasPorAeronave(fechaInicio, fechaFin);

        // Crear reporte
        Reporte reporte = Reporte.builder()
                .tipo(TipoReporte.FLOTA)
                .descripcion("Reporte de uso de flota")
                .fechaInicioRango(fechaInicio)
                .fechaFinRango(fechaFin)
                .generadoPor(usuario)
                .datosAgregados(estadisticas.toString()) // Simplificado, en producción sería JSON
                .numeroRegistros(obtenerMantenimientosPorFlota(fechaInicio, fechaFin).size())
                .build();

        Reporte reporteGuardado = reporteRepository.save(reporte);
        log.info("Reporte de flota creado con ID {}", reporteGuardado.getId());

        return reporteMapper.toDTO(reporteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calcularEstadisticasPorAeronave(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Calculando estadísticas por aeronave");

        Map<String, Object> resultado = new HashMap<>();
        List<Object[]> vuelosPorAeronave = vueloRepository.findEstadisticasPorAeronave(fechaInicio, fechaFin);

        List<Map<String, Object>> estadisticas = new ArrayList<>();
        vuelosPorAeronave.forEach(row -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("aeronaveId", row[0]);
            stat.put("aeronaveMatricula", row[1]);
            stat.put("totalVuelos", row[2]);
            stat.put("horasVuelo", row[3]);
            estadisticas.add(stat);
        });

        resultado.put("estadisticasPorAeronave", estadisticas);
        resultado.put("totalAeronaves", aeronaveRepository.count());
        resultado.put("disponibilidad", calcularDisponibilidadFlota());

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerMantenimientosPorFlota(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo mantenimientos de flota");

        return mantenimientoRepository.findByFechaInicioBetween(fechaInicio, fechaFin)
                .stream()
                .map(mant -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", mant.getId());
                    map.put("aeronaveMatricula", mant.getAeronave().getMatricula());
                    map.put("tipo", mant.getTipo());
                    map.put("descripcion", mant.getDescripcion());
                    map.put("fechaInicio", mant.getFechaInicio());
                    map.put("fechaFin", mant.getFechaFin());
                    map.put("costo", mant.getCosto());
                    map.put("completado", mant.getCompletado());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasCombustible(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Calculando estadísticas de combustible");

        Map<String, Object> resultado = new HashMap<>();
        Double totalLitros = repostajeRepository.findByFechaRepostajeBetween(fechaInicio, fechaFin)
                .stream()
                .mapToDouble(r -> r.getCantidadLitros() != null ? r.getCantidadLitros() : 0.0)
                .sum();

        Double costoTotal = repostajeRepository.findByFechaRepostajeBetween(fechaInicio, fechaFin)
                .stream()
                .mapToDouble(r -> r.getCostoTotal() != null ? r.getCostoTotal() : 0.0)
                .sum();

        resultado.put("totalLitros", totalLitros);
        resultado.put("costoTotal", costoTotal);
        resultado.put("promedioPorLitro", costoTotal / (totalLitros > 0 ? totalLitros : 1));

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calcularDisponibilidadFlota() {
        log.info("Calculando disponibilidad de flota");

        Map<String, Object> resultado = new HashMap<>();
        long disponibles = aeronaveRepository.countByEstado(EstadoAeronave.DISPONIBLE);
        long total = aeronaveRepository.count();

        resultado.put("disponibles", disponibles);
        resultado.put("total", total);
        resultado.put("porcentajeDisponibilidad", total > 0 ? (disponibles * 100.0) / total : 0.0);

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularHorasVueloAeronave(Long aeronaveId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Calculando horas de vuelo para aeronave {}", aeronaveId);

        return vueloRepository.findByAeronaveIdAndFechaSalidaRealBetween(aeronaveId, fechaInicio, fechaFin)
                .stream()
                .mapToDouble(vuelo -> {
                    if (vuelo.getFechaSalidaReal() != null && vuelo.getFechaLlegadaReal() != null) {
                        return java.time.Duration.between(
                                vuelo.getFechaSalidaReal(),
                                vuelo.getFechaLlegadaReal())
                                .toMinutes() / 60.0;
                    }
                    return 0.0;
                })
                .sum();
    }
}
