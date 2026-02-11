package com.paeldav.backend.reportes;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.impl.ReporteHorasServiceImpl;
import com.paeldav.backend.domain.entity.*;
import com.paeldav.backend.domain.enums.EstadoVuelo;
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
 * Pruebas unitarias para el servicio de reportes de horas trabajadas.
 * Enfocado en validación de consistencia de datos administrativos.
 */
@ExtendWith(MockitoExtension.class)
class ReporteHorasServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private RegistroHorasVueloRepository registroHorasVueloRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ReporteMapper reporteMapper;

    @InjectMocks
    private ReporteHorasServiceImpl reporteHorasService;

    private Usuario usuarioTest;
    private Tripulante tripulanteTest;
    private Vuelo vueloTest;
    private RegistroHorasVuelo registroTest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Piloto Test")
                .email("piloto@example.com")
                .build();

        tripulanteTest = Tripulante.builder()
                .id(1L)
                .usuario(usuarioTest)
                .numeroLicencia("LIC123")
                .esPiloto(true)
                .build();

        vueloTest = Vuelo.builder()
                .id(1L)
                .origen("Madrid")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().minusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now())
                .estado(EstadoVuelo.COMPLETADO)
                .build();

        registroTest = RegistroHorasVuelo.builder()
                .id(1L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(2.0)
                .funcionDesempenada("PILOTO_COMANDANTE")
                .tipoVuelo("DIURNO")
                .aprobado(true)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    void testGenerarReporteHorasExitoso() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        List<RegistroHorasVuelo> registros = Arrays.asList(registroTest);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(registroHorasVueloRepository.findByFechaRegistroBetween(inicio, fin))
                .thenReturn(registros);
        when(reporteRepository.save(any(Reporte.class)))
                .thenReturn(Reporte.builder().id(1L).tipo(TipoReporte.HORAS).build());
        when(reporteMapper.toDTO(any(Reporte.class)))
                .thenReturn(ReporteDTO.builder().id(1L).tipo(TipoReporte.HORAS).build());

        // When
        ReporteDTO resultado = reporteHorasService.generarReporteHorasTrabajadas(inicio, fin, 1L);

        // Then
        assertNotNull(resultado);
        assertEquals(TipoReporte.HORAS, resultado.getTipo());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    void testValidarConsistenciaDatosValido() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();
        List<RegistroHorasVuelo> registros = Arrays.asList(registroTest);

        when(registroHorasVueloRepository.findByFechaRegistroBetween(inicio, fin))
                .thenReturn(registros);

        // When
        Map<String, Object> resultado = reporteHorasService.validarConsistenciaDatos(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertTrue((boolean) resultado.get("esValido"));
        assertEquals(0, ((List<?>) resultado.get("anomalias")).size());
        assertEquals(1, resultado.get("registrosValidados"));
    }

    @Test
    void testValidarConsistenciaDatosConHorasNegativas() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();
        
        RegistroHorasVuelo registroNegativo = RegistroHorasVuelo.builder()
                .id(2L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(-5.0) // Horas negativas
                .aprobado(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        List<RegistroHorasVuelo> registros = Arrays.asList(registroNegativo);
        when(registroHorasVueloRepository.findByFechaRegistroBetween(inicio, fin))
                .thenReturn(registros);

        // When
        Map<String, Object> resultado = reporteHorasService.validarConsistenciaDatos(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertFalse((boolean) resultado.get("esValido"));
        assertEquals(1, ((List<?>) resultado.get("anomalias")).size());
    }

    @Test
    void testValidarConsistenciaDatosConRegistrosPendientes() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();
        
        RegistroHorasVuelo registroPendiente = RegistroHorasVuelo.builder()
                .id(3L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(2.0)
                .aprobado(false) // No aprobado
                .fechaRegistro(LocalDateTime.now())
                .build();

        List<RegistroHorasVuelo> registros = Arrays.asList(registroPendiente);
        when(registroHorasVueloRepository.findByFechaRegistroBetween(inicio, fin))
                .thenReturn(registros);

        // When
        Map<String, Object> resultado = reporteHorasService.validarConsistenciaDatos(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertFalse((boolean) resultado.get("esValido"));
        assertTrue(((List<?>) resultado.get("anomalias")).stream()
                .anyMatch(a -> a.toString().contains("pendiente de aprobación")));
    }

    @Test
    void testValidarConsistenciaVueloValido() {
        // Given: vuelo con fechas válidas
        Vuelo vuelo = Vuelo.builder()
                .id(1L)
                .fechaSalidaProgramada(LocalDateTime.now().minusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now())
                .build();

        // When
        boolean resultado = reporteHorasService.validarConsistenciaVuelo(vuelo);

        // Then
        assertTrue(resultado);
    }

    @Test
    void testValidarConsistenciaVueloFechasInvertidas() {
        // Given: vuelo con salida después de llegada
        Vuelo vuelo = Vuelo.builder()
                .id(1L)
                .fechaSalidaProgramada(LocalDateTime.now())
                .fechaLlegadaProgramada(LocalDateTime.now().minusHours(1))
                .build();

        // When
        boolean resultado = reporteHorasService.validarConsistenciaVuelo(vuelo);

        // Then
        assertFalse(resultado);
    }

    @Test
    void testValidarConsistenciaVueloFechaRealAntes() {
        // Given: vuelo con fecha real antes de la programada
        Vuelo vuelo = Vuelo.builder()
                .id(1L)
                .fechaSalidaProgramada(LocalDateTime.now().minusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now())
                .fechaSalidaReal(LocalDateTime.now().minusHours(3))
                .build();

        // When
        boolean resultado = reporteHorasService.validarConsistenciaVuelo(vuelo);

        // Then
        assertFalse(resultado);
    }

    @Test
    void testValidarConsistenciaVueloNull() {
        // When
        boolean resultado = reporteHorasService.validarConsistenciaVuelo(null);

        // Then
        assertFalse(resultado);
    }

    @Test
    void testCalcularHorasPorTripulante() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();
        List<RegistroHorasVuelo> registros = Arrays.asList(registroTest);

        when(registroHorasVueloRepository.findByFechaRegistroBetween(inicio, fin))
                .thenReturn(registros);

        // When
        Map<String, Object> resultado = reporteHorasService.calcularHorasPorTripulante(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.get("horasPorTripulante"));
        assertEquals(2.0, resultado.get("totalHoras"));
    }

    @Test
    void testObtenerRegistrosPendientesAprobacion() {
        // Given
        RegistroHorasVuelo registroPendiente = RegistroHorasVuelo.builder()
                .id(3L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(2.0)
                .aprobado(false)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(registroHorasVueloRepository.findByAprobadoFalse())
                .thenReturn(Arrays.asList(registroPendiente));

        // When
        List<Map<String, Object>> resultado = reporteHorasService.obtenerRegistrosPendientesAprobacion();

        // Then
        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).get("id"));
    }

    @Test
    void testCalcularEstadisticasTiposVuelo() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        RegistroHorasVuelo registroDiurno = RegistroHorasVuelo.builder()
                .id(1L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(3.0)
                .tipoVuelo("DIURNO")
                .aprobado(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        RegistroHorasVuelo registroNocturno = RegistroHorasVuelo.builder()
                .id(2L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(2.0)
                .tipoVuelo("NOCTURNO")
                .aprobado(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(registroHorasVueloRepository.findByFechaRegistroBetween(inicio, fin))
                .thenReturn(Arrays.asList(registroDiurno, registroNocturno));

        // When
        Map<String, Object> resultado = reporteHorasService.calcularEstadisticasTiposVuelo(inicio, fin);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.get("horasPorTipoVuelo"));
    }

    @Test
    void testGenerarReporteUsuarioNoEncontrado() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsuarioNoEncontradoException.class,
                () -> reporteHorasService.generarReporteHorasTrabajadas(inicio, fin, 999L));
    }
}
