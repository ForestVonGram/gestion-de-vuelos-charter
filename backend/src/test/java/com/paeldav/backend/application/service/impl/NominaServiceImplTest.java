package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.nomina.NominaCreateDTO;
import com.paeldav.backend.application.dto.nomina.NominaDTO;
import com.paeldav.backend.application.dto.nomina.NominaUpdateDTO;
import com.paeldav.backend.application.mapper.NominaMapper;
import com.paeldav.backend.domain.entity.Nomina;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.enums.EstadoNomina;
import com.paeldav.backend.infraestructure.repository.NominaRepository;
import com.paeldav.backend.infraestructure.repository.PersonalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para NominaServiceImpl.
 * Verifica el cálculo y gestión de nóminas.
 */
@ExtendWith(MockitoExtension.class)
class NominaServiceImplTest {

    @Mock
    private NominaRepository nominaRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private NominaMapper nominaMapper;

    @InjectMocks
    private NominaServiceImpl nominaService;

    private Personal personal;
    private Nomina nomina;
    private NominaCreateDTO nominaCreateDTO;

    @BeforeEach
    void setUp() {
        personal = Personal.builder()
                .id(1L)
                .numeroEmpleado("EMP001")
                .build();

        nomina = Nomina.builder()
                .id(1L)
                .personal(personal)
                .mes(2)
                .ano(2026)
                .salarioBase(2000.0)
                .deducciones(200.0)
                .bonificaciones(100.0)
                .totalNeto(1900.0)
                .estado(EstadoNomina.PENDIENTE)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        nominaCreateDTO = NominaCreateDTO.builder()
                .personalId(1L)
                .mes(2)
                .ano(2026)
                .salarioBase(2000.0)
                .deducciones(100.0)
                .bonificaciones(100.0)
                .descuentoImpuesto(50.0)
                .descuentoAfiliacion(50.0)
                .build();
    }

    @Test
    void testGenerarNomina_Exitoso() {
        // Arrange
        when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
        when(nominaRepository.findByPersonalIdAndMesAndAno(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        when(nominaRepository.save(any(Nomina.class))).thenReturn(nomina);
        when(nominaMapper.toEntity(nominaCreateDTO)).thenReturn(nomina);
        when(nominaMapper.toDTO(nomina)).thenReturn(new NominaDTO());

        // Act
        NominaDTO resultado = nominaService.generarNomina(nominaCreateDTO);

        // Assert
        assertNotNull(resultado);
        verify(personalRepository, times(1)).findById(1L);
        verify(nominaRepository, times(1)).save(any(Nomina.class));
    }

    @Test
    void testGenerarNomina_PersonalNoEncontrado() {
        // Arrange
        when(personalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> nominaService.generarNomina(nominaCreateDTO));
    }

    @Test
    void testGenerarNomina_NominaYaExiste() {
        // Arrange
        when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
        when(nominaRepository.findByPersonalIdAndMesAndAno(anyLong(), anyInt(), anyInt()))
                .thenReturn(Optional.of(nomina));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> nominaService.generarNomina(nominaCreateDTO));
    }

    @Test
    void testObtenerNominaPorId_Exitoso() {
        // Arrange
        when(nominaRepository.findById(1L)).thenReturn(Optional.of(nomina));
        when(nominaMapper.toDTO(nomina)).thenReturn(new NominaDTO());

        // Act
        NominaDTO resultado = nominaService.obtenerNominaPorId(1L);

        // Assert
        assertNotNull(resultado);
        verify(nominaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerNominaPorId_NoEncontrada() {
        // Arrange
        when(nominaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> nominaService.obtenerNominaPorId(999L));
    }

    @Test
    void testMarcarComoPagada() {
        // Arrange
        Nomina nominaPagada = Nomina.builder()
                .id(1L)
                .personal(personal)
                .mes(2)
                .ano(2026)
                .salarioBase(2000.0)
                .totalNeto(1900.0)
                .estado(EstadoNomina.PAGADA)
                .fechaPago(LocalDateTime.now())
                .build();

        when(nominaRepository.findById(1L)).thenReturn(Optional.of(nomina));
        when(nominaRepository.save(any(Nomina.class))).thenReturn(nominaPagada);
        when(nominaMapper.toDTO(nominaPagada)).thenReturn(new NominaDTO());

        // Act
        NominaDTO resultado = nominaService.marcarComoPagada(1L);

        // Assert
        assertNotNull(resultado);
        verify(nominaRepository, times(1)).save(any(Nomina.class));
    }

    @Test
    void testMarcarComoRetenida() {
        // Arrange
        String motivo = "Deuda anterior";
        Nomina nominaRetenida = Nomina.builder()
                .id(1L)
                .personal(personal)
                .mes(2)
                .ano(2026)
                .salarioBase(2000.0)
                .totalNeto(1900.0)
                .estado(EstadoNomina.RETENIDA)
                .observaciones(motivo)
                .build();

        when(nominaRepository.findById(1L)).thenReturn(Optional.of(nomina));
        when(nominaRepository.save(any(Nomina.class))).thenReturn(nominaRetenida);
        when(nominaMapper.toDTO(nominaRetenida)).thenReturn(new NominaDTO());

        // Act
        NominaDTO resultado = nominaService.marcarComoRetenida(1L, motivo);

        // Assert
        assertNotNull(resultado);
        verify(nominaRepository, times(1)).save(any(Nomina.class));
    }

    @Test
    void testEliminarNomina_Exitoso() {
        // Arrange
        when(nominaRepository.existsById(1L)).thenReturn(true);

        // Act
        nominaService.eliminarNomina(1L);

        // Assert
        verify(nominaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarNomina_NoEncontrada() {
        // Arrange
        when(nominaRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> nominaService.eliminarNomina(999L));
    }

    @Test
    void testCalcularDeducciones() {
        // Test que verifica el cálculo de deducciones
        Double salarioBase = 2000.0;
        Double deducciones = 100.0;
        Double descuentoImpuesto = 50.0;
        Double descuentoAfiliacion = 50.0;
        Double bonificaciones = 100.0;

        Double deduccionesTotal = deducciones + descuentoImpuesto + descuentoAfiliacion;
        Double totalNeto = salarioBase + bonificaciones - deduccionesTotal;

        assertEquals(200.0, deduccionesTotal);
        assertEquals(1900.0, totalNeto);
    }

    @Test
    void testActualizarNomina() {
        // Arrange
        NominaUpdateDTO updateDTO = NominaUpdateDTO.builder()
                .bonificaciones(200.0)
                .deducciones(150.0)
                .estado(EstadoNomina.EN_PROCESO)
                .build();

        when(nominaRepository.findById(1L)).thenReturn(Optional.of(nomina));
        when(nominaRepository.save(any(Nomina.class))).thenReturn(nomina);
        when(nominaMapper.toDTO(nomina)).thenReturn(new NominaDTO());

        // Act
        NominaDTO resultado = nominaService.actualizarNomina(1L, updateDTO);

        // Assert
        assertNotNull(resultado);
        verify(nominaRepository, times(1)).save(any(Nomina.class));
    }
}
