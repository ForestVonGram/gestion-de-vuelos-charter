package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.metricas.MetricasDTO;
import com.paeldav.backend.domain.entity.Pago;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.infraestructure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para MetricasServiceImpl.
 * Verifica el cálculo correcto de métricas del sistema.
 */
@ExtendWith(MockitoExtension.class)
class MetricasServiceImplTest {

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private RegistroHorasVueloRepository registroHorasVueloRepository;

    @InjectMocks
    private MetricasServiceImpl metricasService;

    @Test
    void testObtenerMetricasGenerales() {
        // Arrange
        when(vueloRepository.count()).thenReturn(100L);
        when(vueloRepository.findByEstado(EstadoVuelo.COMPLETADO)).thenReturn(new ArrayList<>());
        when(vueloRepository.findByEstado(EstadoVuelo.EN_CURSO)).thenReturn(new ArrayList<>());
        when(vueloRepository.findByEstado(EstadoVuelo.CANCELADO)).thenReturn(new ArrayList<>());
        when(vueloRepository.findByEstado(EstadoVuelo.CONFIRMADO)).thenReturn(new ArrayList<>());
        when(aeronaveRepository.count()).thenReturn(10L);
        when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE)).thenReturn(new ArrayList<>());
        when(aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO)).thenReturn(new ArrayList<>());
        when(tripulanteRepository.count()).thenReturn(50L);
        when(tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE)).thenReturn(new ArrayList<>());
        when(tripulanteRepository.findByEstado(EstadoTripulante.EN_VUELO)).thenReturn(new ArrayList<>());
        when(personalRepository.count()).thenReturn(60L);
        when(personalRepository.findAll()).thenReturn(new ArrayList<>());
        when(pagoRepository.findAll()).thenReturn(new ArrayList<>());
        when(registroHorasVueloRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        MetricasDTO metricas = metricasService.obtenerMetricasGenerales();

        // Assert
        assertNotNull(metricas);
        assertNotNull(metricas.getMetricasVuelos());
        assertNotNull(metricas.getMetricasFlota());
        assertNotNull(metricas.getMetricasPersonal());
    }

    @Test
    void testCalcularDisponibilidadFlota() {
        // Arrange
        when(aeronaveRepository.count()).thenReturn(10L);
        when(aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO)).thenReturn(new ArrayList<>());

        // Act
        Double disponibilidad = metricasService.calcularDisponibilidadFlota();

        // Assert
        assertEquals(100.0, disponibilidad);
    }

    @Test
    void testCalcularDisponibilidadFlota_ConAeronavesEnMantenimiento() {
        // Arrange
        when(aeronaveRepository.count()).thenReturn(10L);
        List<Object> enMantenimiento = new ArrayList<>();
        enMantenimiento.add("aero1");
        enMantenimiento.add("aero2");
        when(aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO)).thenReturn((List) enMantenimiento);

        // Act
        Double disponibilidad = metricasService.calcularDisponibilidadFlota();

        // Assert
        assertEquals(80.0, disponibilidad);
    }

    @Test
    void testCalcularDisponibilidadFlota_SinAeronaves() {
        // Arrange
        when(aeronaveRepository.count()).thenReturn(0L);

        // Act
        Double disponibilidad = metricasService.calcularDisponibilidadFlota();

        // Assert
        assertEquals(0.0, disponibilidad);
    }

    @Test
    void testCalcularTotalIngresos() {
        // Arrange
        List<Pago> pagos = new ArrayList<>();
        Pago pago1 = new Pago();
        pago1.setMonto(1000.0);
        Pago pago2 = new Pago();
        pago2.setMonto(2000.0);
        pagos.add(pago1);
        pagos.add(pago2);

        when(pagoRepository.findAll()).thenReturn(pagos);

        // Act
        Double ingresos = metricasService.calcularTotalIngresos();

        // Assert
        assertEquals(3000.0, ingresos);
    }

    @Test
    void testCalcularTotalIngresos_SinPagos() {
        // Arrange
        when(pagoRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Double ingresos = metricasService.calcularTotalIngresos();

        // Assert
        assertEquals(0.0, ingresos);
    }

    @Test
    void testObtenerTripulantesDisponibles() {
        // Arrange
        List<Object> tripulantesDisponibles = new ArrayList<>();
        tripulantesDisponibles.add("trip1");
        tripulantesDisponibles.add("trip2");
        tripulantesDisponibles.add("trip3");

        when(tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE))
                .thenReturn((List) tripulantesDisponibles);

        // Act
        Long resultado = metricasService.obtenerTripulantesDisponibles();

        // Assert
        assertEquals(3L, resultado);
    }
}
