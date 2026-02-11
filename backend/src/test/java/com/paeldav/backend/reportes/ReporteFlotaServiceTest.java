package com.paeldav.backend.reportes;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.impl.ReporteFlotaServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoReporte;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de reportes de flota.
 */
@ExtendWith(MockitoExtension.class)
class ReporteFlotaServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private RepostajeRepository repostajeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ReporteMapper reporteMapper;

    @InjectMocks
    private ReporteFlotaServiceImpl reporteFlotaService;

    private Usuario usuarioTest;
    private Aeronave aeronaveTest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Admin Test")
                .email("admin@example.com")
                .build();

        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("EC-ABC")
                .modelo("Boeing 737")
                .capacidadPasajeros(150)
                .capacidadTripulacion(6)
                .estado(EstadoAeronave.DISPONIBLE)
                .horasVueloTotales(1000.0)
                .build();
    }

    @Test
    void testGenerarReporteUsoFlotaExitoso() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(vueloRepository.findEstadisticasPorAeronave(inicio, fin))
                .thenReturn(new ArrayList<>());
        when(aeronaveRepository.count()).thenReturn(1L);
        when(mantenimientoRepository.findByFechaInicioBetween(inicio, fin))
                .thenReturn(new ArrayList<>());
        when(reporteRepository.save(any(Reporte.class)))
                .thenReturn(Reporte.builder().id(1L).tipo(TipoReporte.FLOTA).build());
        when(reporteMapper.toDTO(any(Reporte.class)))
                .thenReturn(ReporteDTO.builder().id(1L).tipo(TipoReporte.FLOTA).build());

        // When
        ReporteDTO resultado = reporteFlotaService.generarReporteUsoFlota(inicio, fin, 1L);

        // Then
        assertNotNull(resultado);
        assertEquals(TipoReporte.FLOTA, resultado.getTipo());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    void testCalcularEstadisticasPorAeronave() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        List<Object[]> estadisticas = new ArrayList<>();
        estadisticas.add(new Object[]{1L, "EC-ABC", 5L, 10.5});

        when(vueloRepository.findEstadisticasPorAeronave(inicio, fin))
                .thenReturn(estadisticas);
        when(aeronaveRepository.count()).thenReturn(1L);
        when(aeronaveRepository.countByEstado(EstadoAeronave.DISPONIBLE)).thenReturn(1L);

        // When
        Map<String, Object> resultado = reporteFlotaService.calcularEstadisticasPorAeronave(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.get("estadisticasPorAeronave"));
        assertEquals(1L, resultado.get("totalAeronaves"));
        assertNotNull(resultado.get("disponibilidad"));
    }

    @Test
    void testObtenerMantenimientosPorFlota() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(mantenimientoRepository.findByFechaInicioBetween(inicio, fin))
                .thenReturn(new ArrayList<>());

        // When
        List<Map<String, Object>> resultado = reporteFlotaService.obtenerMantenimientosPorFlota(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    void testObtenerEstadisticasCombustible() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(repostajeRepository.findByFechaRepostajeBetween(inicio, fin))
                .thenReturn(new ArrayList<>());

        // When
        Map<String, Object> resultado = reporteFlotaService.obtenerEstadisticasCombustible(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertEquals(0.0, resultado.get("totalLitros"));
        assertEquals(0.0, resultado.get("costoTotal"));
    }

    @Test
    void testCalcularDisponibilidadFlota() {
        // Given
        when(aeronaveRepository.countByEstado(EstadoAeronave.DISPONIBLE)).thenReturn(3L);
        when(aeronaveRepository.count()).thenReturn(5L);

        // When
        Map<String, Object> resultado = reporteFlotaService.calcularDisponibilidadFlota();

        // Then
        assertNotNull(resultado);
        assertEquals(3L, resultado.get("disponibles"));
        assertEquals(5L, resultado.get("total"));
        assertEquals(60.0, resultado.get("porcentajeDisponibilidad"));
    }

    @Test
    void testCalcularDisponibilidadFlotaSinAeronaves() {
        // Given
        when(aeronaveRepository.countByEstado(EstadoAeronave.DISPONIBLE)).thenReturn(0L);
        when(aeronaveRepository.count()).thenReturn(0L);

        // When
        Map<String, Object> resultado = reporteFlotaService.calcularDisponibilidadFlota();

        // Then
        assertNotNull(resultado);
        assertEquals(0L, resultado.get("disponibles"));
        assertEquals(0L, resultado.get("total"));
        assertEquals(0.0, resultado.get("porcentajeDisponibilidad"));
    }

    @Test
    void testCalcularHorasVueloAeronave() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(vueloRepository.findByAeronaveIdAndFechaSalidaRealBetween(1L, inicio, fin))
                .thenReturn(new ArrayList<>());

        // When
        Double resultado = reporteFlotaService.calcularHorasVueloAeronave(1L, inicio, fin);

        // Then
        assertNotNull(resultado);
        assertEquals(0.0, resultado);
    }

    @Test
    void testGenerarReporteUsuarioNoEncontrado() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsuarioNoEncontradoException.class,
                () -> reporteFlotaService.generarReporteUsoFlota(inicio, fin, 999L));
    }
}
