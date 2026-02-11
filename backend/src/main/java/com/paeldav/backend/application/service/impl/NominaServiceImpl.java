package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.nomina.NominaCreateDTO;
import com.paeldav.backend.application.dto.nomina.NominaDTO;
import com.paeldav.backend.application.dto.nomina.NominaFiltroDTO;
import com.paeldav.backend.application.dto.nomina.NominaUpdateDTO;
import com.paeldav.backend.application.mapper.NominaMapper;
import com.paeldav.backend.application.service.base.NominaService;
import com.paeldav.backend.domain.entity.Nomina;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.enums.EstadoNomina;
import com.paeldav.backend.infraestructure.repository.NominaRepository;
import com.paeldav.backend.infraestructure.repository.PersonalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Nómina.
 * Proporciona la lógica de negocio para gestionar nóminas del personal.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NominaServiceImpl implements NominaService {

    private final NominaRepository nominaRepository;
    private final PersonalRepository personalRepository;
    private final NominaMapper nominaMapper;

    @Override
    public NominaDTO generarNomina(NominaCreateDTO nominaCreateDTO) {
        Personal personal = personalRepository.findById(nominaCreateDTO.getPersonalId())
                .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));

        // Verificar que no exista una nómina para ese período
        nominaRepository.findByPersonalIdAndMesAndAno(
                nominaCreateDTO.getPersonalId(),
                nominaCreateDTO.getMes(),
                nominaCreateDTO.getAno()
        ).ifPresent(n -> {
            throw new IllegalStateException("Ya existe una nómina para este período");
        });

        Nomina nomina = nominaMapper.toEntity(nominaCreateDTO);
        nomina.setPersonal(personal);
        
        // Calcular deducciones totales
        Double deduccionesTotal = nominaCreateDTO.getDeducciones() +
                nominaCreateDTO.getDescuentoImpuesto() +
                nominaCreateDTO.getDescuentoAfiliacion();
        nomina.setDeducciones(deduccionesTotal);
        nomina.setDescuentoImpuesto(nominaCreateDTO.getDescuentoImpuesto());
        nomina.setDescuentoAfiliacion(nominaCreateDTO.getDescuentoAfiliacion());

        Nomina nominaGuardada = nominaRepository.save(nomina);
        return nominaMapper.toDTO(nominaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public NominaDTO obtenerNominaPorId(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada"));
        return nominaMapper.toDTO(nomina);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NominaDTO> obtenerNominasPorPersonal(Long personalId) {
        List<Nomina> nominas = nominaRepository.findByPersonalIdOrderByAnoDescMesDesc(personalId);
        return nominaMapper.toDTOList(nominas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NominaDTO> obtenerNominasPorPeriodo(Integer mes, Integer ano) {
        List<Nomina> nominas = nominaRepository.findByMesAndAno(mes, ano);
        return nominaMapper.toDTOList(nominas);
    }

    @Override
    @Transactional(readOnly = true)
    public NominaDTO obtenerNominaPorPersonalYPeriodo(Long personalId, Integer mes, Integer ano) {
        Nomina nomina = nominaRepository.findByPersonalIdAndMesAndAno(personalId, mes, ano)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada para el período especificado"));
        return nominaMapper.toDTO(nomina);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NominaDTO> obtenerNominasPorEstado(EstadoNomina estado) {
        List<Nomina> nominas = nominaRepository.findByEstado(estado);
        return nominaMapper.toDTOList(nominas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NominaDTO> obtenerNominasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        List<Nomina> nominas = nominaRepository.findByFechaGeneracionBetween(fechaInicio, fechaFin);
        return nominaMapper.toDTOList(nominas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NominaDTO> listarNominasConFiltro(NominaFiltroDTO filtro) {
        List<Nomina> nominas = nominaRepository.findAll().stream()
                .filter(n -> filtro.getPersonalId() == null || n.getPersonal().getId().equals(filtro.getPersonalId()))
                .filter(n -> filtro.getMes() == null || n.getMes().equals(filtro.getMes()))
                .filter(n -> filtro.getAno() == null || n.getAno().equals(filtro.getAno()))
                .filter(n -> filtro.getEstado() == null || n.getEstado().equals(filtro.getEstado()))
                .collect(Collectors.toList());
        return nominaMapper.toDTOList(nominas);
    }

    @Override
    public NominaDTO actualizarNomina(Long id, NominaUpdateDTO nominaUpdateDTO) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada"));

        nominaMapper.updateEntityFromDTO(nominaUpdateDTO, nomina);

        // Si se actualiza bonificaciones o deducciones adicionales, recalcular deducciones totales
        if (nominaUpdateDTO.getDeducciones() != null || 
            nominaUpdateDTO.getDescuentoImpuesto() != null ||
            nominaUpdateDTO.getDescuentoAfiliacion() != null) {
            
            Double deduccionesTotal = (nominaUpdateDTO.getDeducciones() != null ? nominaUpdateDTO.getDeducciones() : nomina.getDeducciones());
            Double descuentoImpuesto = nominaUpdateDTO.getDescuentoImpuesto() != null ? nominaUpdateDTO.getDescuentoImpuesto() : nomina.getDescuentoImpuesto();
            Double descuentoAfiliacion = nominaUpdateDTO.getDescuentoAfiliacion() != null ? nominaUpdateDTO.getDescuentoAfiliacion() : nomina.getDescuentoAfiliacion();
            
            nomina.setDeducciones(deduccionesTotal + descuentoImpuesto + descuentoAfiliacion);
            nomina.setDescuentoImpuesto(descuentoImpuesto);
            nomina.setDescuentoAfiliacion(descuentoAfiliacion);
        }

        Nomina nominaActualizada = nominaRepository.save(nomina);
        return nominaMapper.toDTO(nominaActualizada);
    }

    @Override
    public NominaDTO marcarComoPagada(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada"));

        nomina.setEstado(EstadoNomina.PAGADA);
        nomina.setFechaPago(LocalDateTime.now());

        Nomina nominaActualizada = nominaRepository.save(nomina);
        return nominaMapper.toDTO(nominaActualizada);
    }

    @Override
    public NominaDTO marcarComoRetenida(Long id, String motivo) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada"));

        nomina.setEstado(EstadoNomina.RETENIDA);
        nomina.setObservaciones(motivo);

        Nomina nominaActualizada = nominaRepository.save(nomina);
        return nominaMapper.toDTO(nominaActualizada);
    }

    @Override
    public void eliminarNomina(Long id) {
        if (!nominaRepository.existsById(id)) {
            throw new IllegalArgumentException("Nómina no encontrada");
        }
        nominaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalNominasPagadas(Long personalId) {
        return nominaRepository.sumTotalNetoByPersonalIdAndEstadoPagada(personalId);
    }

    @Override
    public Integer procesarPagosNominaPendientes() {
        List<Nomina> nominasPendientes = nominaRepository.findByEstado(EstadoNomina.PENDIENTE);
        
        int procesadas = 0;
        for (Nomina nomina : nominasPendientes) {
            nomina.setEstado(EstadoNomina.EN_PROCESO);
            nominaRepository.save(nomina);
            procesadas++;
        }
        
        return procesadas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NominaDTO> obtenerHistorialNominas(Long personalId) {
        return obtenerNominasPorPersonal(personalId);
    }
}
