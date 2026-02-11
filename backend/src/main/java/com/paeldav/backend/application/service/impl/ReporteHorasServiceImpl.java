package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.base.ReporteHorasService;
import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.entity.RegistroHorasVuelo;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
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
 * Implementación del servicio de reportes de horas trabajadas.
 * Genera reportes sobre actividad laboral de tripulación y valida consistencia de datos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReporteHorasServiceImpl implements ReporteHorasService {

    private final ReporteRepository reporteRepository;
    private final RegistroHorasVueloRepository registroHorasVueloRepository;
    private final VueloRepository vueloRepository;
    private final TripulanteRepository tripulanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteMapper reporteMapper;

    @Override
    public ReporteDTO generarReporteHorasTrabajadas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long usuarioIdAutenticado) {
        log.info("Generando reporte de horas trabajadas desde {} hasta {}", fechaInicio, fechaFin);

        Usuario usuario = usuarioRepository.findById(usuarioIdAutenticado)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        // Obtener registros de horas
        List<RegistroHorasVuelo> registros = registroHorasVueloRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);

        // Validar consistencia
        Map<String, Object> validacion = validarConsistenciaDatos(fechaInicio, fechaFin);

        // Crear reporte
        Reporte reporte = Reporte.builder()
                .tipo(TipoReporte.HORAS)
                .descripcion("Reporte de horas trabajadas")
                .fechaInicioRango(fechaInicio)
                .fechaFinRango(fechaFin)
                .generadoPor(usuario)
                .datosAgregados(calcularHorasPorTripulante(fechaInicio, fechaFin).toString())
                .numeroRegistros(registros.size())
                .observaciones("Validación: " + validacion.get("esValido"))
                .build();

        Reporte reporteGuardado = reporteRepository.save(reporte);
        log.info("Reporte de horas trabajadas creado con ID {}", reporteGuardado.getId());

        return reporteMapper.toDTO(reporteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calcularHorasPorTripulante(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Calculando horas por tripulante");

        Map<String, Object> resultado = new HashMap<>();
        List<RegistroHorasVuelo> registros = registroHorasVueloRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);

        Map<String, Double> horasPorTripulante = new HashMap<>();
        registros.forEach(reg -> {
            String nombre = reg.getTripulante().getUsuario().getNombre();
            horasPorTripulante.merge(nombre, reg.getHorasVoladas(), Double::sum);
        });

        resultado.put("horasPorTripulante", horasPorTripulante);
        resultado.put("totalHoras", horasPorTripulante.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum());

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calcularHorasPorFuncion(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Calculando horas por función");

        Map<String, Object> resultado = new HashMap<>();
        List<RegistroHorasVuelo> registros = registroHorasVueloRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);

        Map<String, Double> horasPorFuncion = new HashMap<>();
        registros.forEach(reg -> {
            String funcion = reg.getFuncionDesempenada() != null ? reg.getFuncionDesempenada() : "Sin especificar";
            horasPorFuncion.merge(funcion, reg.getHorasVoladas(), Double::sum);
        });

        resultado.put("horasPorFuncion", horasPorFuncion);
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validarConsistenciaDatos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Validando consistencia de datos");

        Map<String, Object> resultado = new HashMap<>();
        List<RegistroHorasVuelo> registros = registroHorasVueloRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);

        // Detectar anomalías
        List<String> anomalias = new ArrayList<>();
        
        registros.forEach(reg -> {
            // Validar horas positivas
            if (reg.getHorasVoladas() <= 0) {
                anomalias.add("Registro " + reg.getId() + ": horas negativas o cero");
            }
            
            // Validar aprobación
            if (!Boolean.TRUE.equals(reg.getAprobado())) {
                anomalias.add("Registro " + reg.getId() + ": pendiente de aprobación");
            }
            
            // Validar consistencia con vuelo
            if (!validarConsistenciaVuelo(reg.getVuelo())) {
                anomalias.add("Registro " + reg.getId() + ": inconsistencia con datos del vuelo");
            }
        });

        resultado.put("esValido", anomalias.isEmpty());
        resultado.put("anomalias", anomalias);
        resultado.put("totalAnomalias", anomalias.size());
        resultado.put("registrosValidados", registros.size());

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarConsistenciaVuelo(Vuelo vuelo) {
        if (vuelo == null) {
            return false;
        }

        // Validar que las fechas del vuelo sean consistentes
        if (vuelo.getFechaSalidaProgramada() == null || vuelo.getFechaLlegadaProgramada() == null) {
            return false;
        }

        if (vuelo.getFechaSalidaProgramada().isAfter(vuelo.getFechaLlegadaProgramada())) {
            return false;
        }

        // Validar que si hay fechas reales, sean después de las programadas
        if (vuelo.getFechaSalidaReal() != null && vuelo.getFechaSalidaReal().isBefore(vuelo.getFechaSalidaProgramada())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerRegistrosPendientesAprobacion() {
        log.info("Obteniendo registros pendientes de aprobación");

        return registroHorasVueloRepository.findByAprobadoFalse()
                .stream()
                .map(reg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", reg.getId());
                    map.put("tripulante", reg.getTripulante().getUsuario().getNombre());
                    map.put("vuelo", reg.getVuelo().getId());
                    map.put("horasVoladas", reg.getHorasVoladas());
                    map.put("funcion", reg.getFuncionDesempenada());
                    map.put("fechaRegistro", reg.getFechaRegistro());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calcularEstadisticasTiposVuelo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Calculando estadísticas de tipos de vuelo");

        Map<String, Object> resultado = new HashMap<>();
        List<RegistroHorasVuelo> registros = registroHorasVueloRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);

        Map<String, Double> horasPorTipoVuelo = new HashMap<>();
        registros.forEach(reg -> {
            String tipoVuelo = reg.getTipoVuelo() != null ? reg.getTipoVuelo() : "Sin especificar";
            horasPorTipoVuelo.merge(tipoVuelo, reg.getHorasVoladas(), Double::sum);
        });

        resultado.put("horasPorTipoVuelo", horasPorTipoVuelo);
        return resultado;
    }
}
