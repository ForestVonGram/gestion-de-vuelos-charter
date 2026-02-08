package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.pago.PagoCreateDTO;
import com.paeldav.backend.application.dto.pago.PagoDTO;
import com.paeldav.backend.application.mapper.PagoMapper;
import com.paeldav.backend.application.service.integration.MercadoPagoService;
import com.paeldav.backend.domain.entity.Pago;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoPago;
import com.paeldav.backend.exception.PagoNoEncontradoException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.PagoRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio PagoServiceImpl.
 */
@DisplayName("Pruebas unitarias de PagoServiceImpl")
class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MercadoPagoService mercadoPagoService;

    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private PagoServiceImpl pagoService;

    private Vuelo testVuelo;
    private Usuario testUsuario;
    private Pago testPago;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testVuelo = Vuelo.builder()
                .id(1L)
                .origen("Bogotá")
                .destino("Cartagena")
                .costoEstimado(100000.0)
                .build();

        testUsuario = Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .build();

        testPago = Pago.builder()
                .id(1L)
                .vuelo(testVuelo)
                .usuario(testUsuario)
                .monto(100000.0)
                .estado(EstadoPago.PENDIENTE)
                .emailCliente("test@example.com")
                .numeroPreferencia("pref123")
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe crear un pago correctamente")
    void testIniciarPago_Success() {
        // Arrange
        PagoCreateDTO pagoCreateDTO = PagoCreateDTO.builder()
                .vueloId(1L)
                .usuarioId(1L)
                .monto(100000.0)
                .emailCliente("test@example.com")
                .descripcion("Pago de vuelo")
                .build();

        when(vueloRepository.findById(1L)).thenReturn(Optional.of(testVuelo));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));
        when(mercadoPagoService.crearPreferencia(anyLong(), anyDouble(), anyString(), anyString()))
                .thenReturn(MercadoPagoService.PreferenciaResponse.builder()
                        .id("pref123")
                        .urlPago("https://mercadopago.com/pref123")
                        .numeroPreferencia("pref123")
                        .build());
        when(pagoRepository.save(any(Pago.class))).thenReturn(testPago);
        when(pagoMapper.toDTO(testPago)).thenReturn(PagoDTO.builder()
                .id(1L)
                .vueloId(1L)
                .estado(EstadoPago.PENDIENTE)
                .build());

        // Act
        PagoDTO result = pagoService.iniciarPago(pagoCreateDTO);

        // Assert
        assertNotNull(result);
        verify(vueloRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(mercadoPagoService).crearPreferencia(1L, 100000.0, "test@example.com", "Pago de vuelo");
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el vuelo no existe")
    void testIniciarPago_VueloNoEncontrado() {
        // Arrange
        PagoCreateDTO pagoCreateDTO = PagoCreateDTO.builder()
                .vueloId(999L)
                .usuarioId(1L)
                .monto(100000.0)
                .emailCliente("test@example.com")
                .build();

        when(vueloRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VueloNoEncontradoException.class,
                () -> pagoService.iniciarPago(pagoCreateDTO));
    }

    @Test
    @DisplayName("Debe obtener pago por ID")
    void testObtenerPagoPorId_Success() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(testPago));
        when(pagoMapper.toDTO(testPago)).thenReturn(PagoDTO.builder()
                .id(1L)
                .vueloId(1L)
                .build());

        // Act
        PagoDTO result = pagoService.obtenerPagoPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(pagoRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si pago no existe")
    void testObtenerPagoPorId_NoEncontrado() {
        // Arrange
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PagoNoEncontradoException.class,
                () -> pagoService.obtenerPagoPorId(999L));
    }

    @Test
    @DisplayName("Debe obtener pagos por vuelo")
    void testObtenerPagosPorVuelo() {
        // Arrange
        List<Pago> pagos = Arrays.asList(testPago);
        when(pagoRepository.findByVueloId(1L)).thenReturn(pagos);
        when(pagoMapper.toDTOList(pagos)).thenReturn(Arrays.asList(
                PagoDTO.builder().id(1L).vueloId(1L).build()
        ));

        // Act
        List<PagoDTO> result = pagoService.obtenerPagosPorVuelo(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pagoRepository).findByVueloId(1L);
    }

    @Test
    @DisplayName("Debe confirmar un pago")
    void testConfirmarPago_Success() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(testPago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(testPago);
        when(pagoMapper.toDTO(testPago)).thenReturn(PagoDTO.builder()
                .id(1L)
                .estado(EstadoPago.CONFIRMADO)
                .build());

        // Act
        PagoDTO result = pagoService.confirmarPago(1L, "mp123456");

        // Assert
        assertNotNull(result);
        verify(pagoRepository).findById(1L);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe rechazar un pago")
    void testRechazarPago_Success() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(testPago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(testPago);
        when(pagoMapper.toDTO(testPago)).thenReturn(PagoDTO.builder()
                .id(1L)
                .estado(EstadoPago.RECHAZADO)
                .build());

        // Act
        PagoDTO result = pagoService.rechazarPago(1L, "Tarjeta rechazada");

        // Assert
        assertNotNull(result);
        verify(pagoRepository).findById(1L);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe reembolsar un pago confirmado")
    void testReembolsarPago_Success() {
        // Arrange
        testPago.setEstado(EstadoPago.CONFIRMADO);
        testPago.setReferenciaMercadoPago("mp123456");
        
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(testPago));
        when(mercadoPagoService.reembolsarPago(anyLong(), anyDouble()))
                .thenReturn(MercadoPagoService.RefundResponse.builder()
                        .refundId(1L)
                        .paymentId(123L)
                        .monto(100000.0)
                        .status("approved")
                        .build());
        when(pagoRepository.save(any(Pago.class))).thenReturn(testPago);
        when(pagoMapper.toDTO(testPago)).thenReturn(PagoDTO.builder()
                .id(1L)
                .estado(EstadoPago.REEMBOLSADO)
                .build());

        // Act
        PagoDTO result = pagoService.reembolsarPago(1L, "Vuelo cancelado");

        // Assert
        assertNotNull(result);
        assertEquals(EstadoPago.REEMBOLSADO, result.getEstado());
        verify(pagoRepository).findById(1L);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe rechazar reembolso si pago no está confirmado")
    void testReembolsarPago_NoConfirmado() {
        // Arrange
        testPago.setEstado(EstadoPago.PENDIENTE);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(testPago));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> pagoService.reembolsarPago(1L, "Motivo"));
    }

    @Test
    @DisplayName("Debe obtener total de pagos confirmados")
    void testObtenerTotalPagosConfirmados() {
        // Arrange
        when(pagoRepository.getTotalConfirmedAmountForFlight(1L)).thenReturn(100000.0);

        // Act
        Double result = pagoService.obtenerTotalPagosConfirmados(1L);

        // Assert
        assertNotNull(result);
        assertEquals(100000.0, result);
        verify(pagoRepository).getTotalConfirmedAmountForFlight(1L);
    }

    @Test
    @DisplayName("Debe verificar si tiene pago confirmado")
    void testTienePagoConfirmado_True() {
        // Arrange
        when(pagoRepository.getTotalConfirmedAmountForFlight(1L)).thenReturn(100000.0);

        // Act
        boolean result = pagoService.tienePagoConfirmado(1L, 50000.0);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe verificar si no tiene pago confirmado suficiente")
    void testTienePagoConfirmado_False() {
        // Arrange
        when(pagoRepository.getTotalConfirmedAmountForFlight(1L)).thenReturn(30000.0);

        // Act
        boolean result = pagoService.tienePagoConfirmado(1L, 50000.0);

        // Assert
        assertFalse(result);
    }
}
