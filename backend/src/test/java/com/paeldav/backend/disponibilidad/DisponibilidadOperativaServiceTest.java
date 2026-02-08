package com.paeldav.backend.disponibilidad;

import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.DisponibilidadAeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.DisponibilidadTripulanteDTO;
import com.paeldav.backend.application.dto.disponibilidad.ResultadoValidacionDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.mapper.TripulanteMapper;
import com.paeldav.backend.application.service.impl.DisponibilidadOperativaServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.ConflictoDisponibilidadException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DisponibilidadOperativaService Tests")
class DisponibilidadOperativaServiceTest {

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private AeronaveMapper aeronaveMapper;

    @Mock
    private TripulanteMapper tripulanteMapper;

    @InjectMocks
    private DisponibilidadOperativaServiceImpl disponibilidadService;

    private Aeronave aeronaveTest;
    private Tripulante tripulanteTest;
    private Usuario usuarioTest;
    private Vuelo vueloExistente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @BeforeEach
    void setUp() {
        fechaInicio = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        fechaFin = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);

        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .activo(true)
                .build();

        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("HK-1234")
                .modelo("Cessna 208")
                .capacidadPasajeros(12)
                .estado(EstadoAeronave.DISPONIBLE)
                .build();

        tripulanteTest = Tripulante.builder()
                .id(1L)
                .usuario(usuarioTest)
                .numeroLicencia("PIL-001")
                .estado(EstadoTripulante.DISPONIBLE)
                .esPiloto(true)
                .build();

        vueloExistente = Vuelo.builder()
                .id(100L)
                .aeronave(aeronaveTest)
                .tripulacion(List.of(tripulanteTest))
                .origen("Bogotá")
                .destino("Medellín")
                .fechaSalidaProgramada(fechaInicio)
                .fechaLlegadaProgramada(fechaFin)
                .estado(EstadoVuelo.CONFIRMADO)
                .build();
    }

    @Nested
    @DisplayName("Validación de Conflictos de Agenda Tests")
    class ValidacionConflictosAgendaTests {

        @Test
        @DisplayName("Validar conflictos - Sin conflictos retorna disponible")
        void validarConflictos_SinConflictos_RetornaDisponible() {
            // Arrange
            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());
            when(vueloRepository.findVuelosEnRangoPorTripulante(anyLong(), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            ResultadoValidacionDTO resultado = disponibilidadService.validarConflictosAgenda(
                    1L, List.of(1L), fechaInicio, fechaFin);

            // Assert
            assertTrue(resultado.isDisponible());
            assertTrue(resultado.getConflictosAeronave().isEmpty());
            assertTrue(resultado.getConflictosTripulacion().isEmpty());
            assertEquals("Todos los recursos están disponibles para el rango solicitado", resultado.getResumen());
        }

        @Test
        @DisplayName("Validar conflictos - Con solapamiento de horarios detecta conflicto")
        void validarConflictos_ConSolapamiento_DetectaConflicto() {
            // Arrange
            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente));

            // Act
            ResultadoValidacionDTO resultado = disponibilidadService.validarConflictosAgenda(
                    1L, Collections.emptyList(), fechaInicio, fechaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertEquals(1, resultado.getConflictosAeronave().size());
            assertEquals(100L, resultado.getConflictosAeronave().get(0).getVueloId());
            assertTrue(resultado.getResumen().contains("1 conflicto"));
        }

        @Test
        @DisplayName("Validar conflictos - Vuelo parcialmente solapado se detecta")
        void validarConflictos_SolapamientoParcial_DetectaConflicto() {
            // Arrange - Vuelo existente de 10:00 a 14:00, consultamos 12:00 a 16:00
            LocalDateTime consultaInicio = fechaInicio.plusHours(2); // 12:00
            LocalDateTime consultaFin = fechaFin.plusHours(2); // 16:00

            when(vueloRepository.findVuelosEnRangoPorAeronave(eq(1L), eq(consultaInicio), eq(consultaFin), anyList()))
                    .thenReturn(List.of(vueloExistente));

            // Act
            ResultadoValidacionDTO resultado = disponibilidadService.validarConflictosAgenda(
                    1L, Collections.emptyList(), consultaInicio, consultaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertEquals(1, resultado.getConflictosAeronave().size());
        }
    }

    @Nested
    @DisplayName("Doble Asignación de Aeronave Tests")
    class DobleAsignacionAeronaveTests {

        @Test
        @DisplayName("Detecta doble asignación de aeronave")
        void consultarDisponibilidad_AeronaveConVueloAsignado_DetectaConflicto() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(eq(1L), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente));

            // Act
            DisponibilidadAeronaveDTO resultado = disponibilidadService.consultarDisponibilidadAeronave(
                    1L, fechaInicio, fechaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertEquals(1, resultado.getConflictos().size());
            assertNotNull(resultado.getMotivoNoDisponible());
            assertTrue(resultado.getMotivoNoDisponible().contains("1 vuelo(s) programado(s)"));
        }

        @Test
        @DisplayName("Aeronave sin vuelos asignados está disponible")
        void consultarDisponibilidad_AeronaveSinVuelos_Disponible() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(eq(1L), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            DisponibilidadAeronaveDTO resultado = disponibilidadService.consultarDisponibilidadAeronave(
                    1L, fechaInicio, fechaFin);

            // Assert
            assertTrue(resultado.isDisponible());
            assertTrue(resultado.getConflictos().isEmpty());
            assertNull(resultado.getMotivoNoDisponible());
        }

        @Test
        @DisplayName("Aeronave en mantenimiento no está disponible")
        void consultarDisponibilidad_AeronaveEnMantenimiento_NoDisponible() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(eq(1L), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            DisponibilidadAeronaveDTO resultado = disponibilidadService.consultarDisponibilidadAeronave(
                    1L, fechaInicio, fechaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertTrue(resultado.getMotivoNoDisponible().contains("EN_MANTENIMIENTO"));
        }

        @Test
        @DisplayName("Múltiples vuelos asignados a la misma aeronave")
        void consultarDisponibilidad_MultiplesVuelos_DetectaTodos() {
            // Arrange
            Vuelo vuelo2 = Vuelo.builder()
                    .id(101L)
                    .origen("Cali")
                    .destino("Cartagena")
                    .fechaSalidaProgramada(fechaInicio.plusHours(1))
                    .fechaLlegadaProgramada(fechaFin.plusHours(1))
                    .estado(EstadoVuelo.SOLICITADO)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(eq(1L), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente, vuelo2));

            // Act
            DisponibilidadAeronaveDTO resultado = disponibilidadService.consultarDisponibilidadAeronave(
                    1L, fechaInicio, fechaFin.plusHours(2));

            // Assert
            assertFalse(resultado.isDisponible());
            assertEquals(2, resultado.getConflictos().size());
            assertTrue(resultado.getMotivoNoDisponible().contains("2 vuelo(s)"));
        }
    }

    @Nested
    @DisplayName("Doble Asignación de Piloto Tests")
    class DobleAsignacionPilotoTests {

        @Test
        @DisplayName("Detecta doble asignación de piloto")
        void consultarDisponibilidad_PilotoConVueloAsignado_DetectaConflicto() {
            // Arrange
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(vueloRepository.findVuelosEnRangoPorTripulante(eq(1L), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente));

            // Act
            DisponibilidadTripulanteDTO resultado = disponibilidadService.consultarDisponibilidadTripulante(
                    1L, fechaInicio, fechaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertEquals(1, resultado.getConflictos().size());
            assertNotNull(resultado.getMotivoNoDisponible());
            assertTrue(resultado.getMotivoNoDisponible().contains("1 vuelo(s) asignado(s)"));
        }

        @Test
        @DisplayName("Piloto sin vuelos asignados está disponible")
        void consultarDisponibilidad_PilotoSinVuelos_Disponible() {
            // Arrange
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(vueloRepository.findVuelosEnRangoPorTripulante(eq(1L), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            DisponibilidadTripulanteDTO resultado = disponibilidadService.consultarDisponibilidadTripulante(
                    1L, fechaInicio, fechaFin);

            // Assert
            assertTrue(resultado.isDisponible());
            assertTrue(resultado.getConflictos().isEmpty());
            assertNull(resultado.getMotivoNoDisponible());
            assertTrue(resultado.isEsPiloto());
        }

        @Test
        @DisplayName("Piloto de descanso no está disponible")
        void consultarDisponibilidad_PilotoDeDescanso_NoDisponible() {
            // Arrange
            tripulanteTest.setEstado(EstadoTripulante.DE_DESCANSO);
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(vueloRepository.findVuelosEnRangoPorTripulante(eq(1L), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            DisponibilidadTripulanteDTO resultado = disponibilidadService.consultarDisponibilidadTripulante(
                    1L, fechaInicio, fechaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertTrue(resultado.getMotivoNoDisponible().contains("DE_DESCANSO"));
        }

        @Test
        @DisplayName("Validar conflictos de múltiples tripulantes")
        void validarConflictos_MultiplesTripulantes_DetectaTodosLosConflictos() {
            // Arrange
            Tripulante copiloto = Tripulante.builder()
                    .id(2L)
                    .usuario(Usuario.builder().id(2L).nombre("María").apellido("García").build())
                    .numeroLicencia("PIL-002")
                    .estado(EstadoTripulante.DISPONIBLE)
                    .esPiloto(true)
                    .build();

            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());
            when(vueloRepository.findVuelosEnRangoPorTripulante(eq(1L), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente));
            when(vueloRepository.findVuelosEnRangoPorTripulante(eq(2L), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente));
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(tripulanteRepository.findById(2L)).thenReturn(Optional.of(copiloto));

            // Act
            ResultadoValidacionDTO resultado = disponibilidadService.validarConflictosAgenda(
                    1L, List.of(1L, 2L), fechaInicio, fechaFin);

            // Assert
            assertFalse(resultado.isDisponible());
            assertEquals(2, resultado.getConflictosTripulacion().size());
        }
    }

    @Nested
    @DisplayName("Consulta de Recursos Disponibles Tests")
    class ConsultaRecursosDisponiblesTests {

        @Test
        @DisplayName("Consultar aeronaves disponibles filtra las ocupadas")
        void consultarAeronavesDisponibles_FiltraOcupadas() {
            // Arrange
            Aeronave aeronave2 = Aeronave.builder()
                    .id(2L)
                    .matricula("HK-5678")
                    .modelo("King Air 350")
                    .capacidadPasajeros(9)
                    .estado(EstadoAeronave.DISPONIBLE)
                    .build();

            AeronaveDTO aeronaveDTO = AeronaveDTO.builder()
                    .id(2L)
                    .matricula("HK-5678")
                    .modelo("King Air 350")
                    .build();

            when(vueloRepository.findAeronaveIdsConVuelosEnRango(any(), any(), anyList()))
                    .thenReturn(List.of(1L)); // aeronaveTest está ocupada
            when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE))
                    .thenReturn(List.of(aeronaveTest, aeronave2));
            when(aeronaveMapper.toDTO(aeronave2)).thenReturn(aeronaveDTO);

            // Act
            List<AeronaveDTO> disponibles = disponibilidadService.consultarAeronavesDisponibles(
                    fechaInicio, fechaFin, null);

            // Assert
            assertEquals(1, disponibles.size());
            assertEquals("HK-5678", disponibles.get(0).getMatricula());
        }

        @Test
        @DisplayName("Consultar aeronaves disponibles filtra por capacidad mínima")
        void consultarAeronavesDisponibles_FiltraPorCapacidad() {
            // Arrange
            Aeronave aeronave2 = Aeronave.builder()
                    .id(2L)
                    .matricula("HK-5678")
                    .modelo("King Air 350")
                    .capacidadPasajeros(9)
                    .estado(EstadoAeronave.DISPONIBLE)
                    .build();

            AeronaveDTO aeronaveDTO = AeronaveDTO.builder()
                    .id(1L)
                    .matricula("HK-1234")
                    .capacidadPasajeros(12)
                    .build();

            when(vueloRepository.findAeronaveIdsConVuelosEnRango(any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());
            when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE))
                    .thenReturn(List.of(aeronaveTest, aeronave2));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTO);

            // Act - Buscar aeronaves con capacidad mínima de 10
            List<AeronaveDTO> disponibles = disponibilidadService.consultarAeronavesDisponibles(
                    fechaInicio, fechaFin, 10);

            // Assert
            assertEquals(1, disponibles.size());
            assertEquals("HK-1234", disponibles.get(0).getMatricula());
        }

        @Test
        @DisplayName("Consultar tripulantes disponibles filtra solo pilotos")
        void consultarTripulantesDisponibles_FiltraSoloPilotos() {
            // Arrange
            Tripulante auxiliar = Tripulante.builder()
                    .id(2L)
                    .usuario(Usuario.builder().nombre("Ana").apellido("López").build())
                    .numeroLicencia("AUX-001")
                    .estado(EstadoTripulante.DISPONIBLE)
                    .esPiloto(false)
                    .build();

            TripulanteDTO tripulanteDTO = TripulanteDTO.builder()
                    .id(1L)
                    .numeroLicencia("PIL-001")
                    .esPiloto(true)
                    .build();

            when(vueloRepository.findTripulanteIdsConVuelosEnRango(any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());
            when(tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE))
                    .thenReturn(List.of(tripulanteTest, auxiliar));
            when(tripulanteMapper.toDTO(tripulanteTest)).thenReturn(tripulanteDTO);

            // Act
            List<TripulanteDTO> disponibles = disponibilidadService.consultarTripulantesDisponibles(
                    fechaInicio, fechaFin, true);

            // Assert
            assertEquals(1, disponibles.size());
            assertTrue(disponibles.get(0).getEsPiloto());
        }
    }

    @Nested
    @DisplayName("Validar y Lanzar Excepción Tests")
    class ValidarYLanzarExcepcionTests {

        @Test
        @DisplayName("Lanza excepción cuando hay conflictos")
        void validarYLanzar_ConConflictos_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), anyList()))
                    .thenReturn(List.of(vueloExistente));

            // Act & Assert
            ConflictoDisponibilidadException exception = assertThrows(
                    ConflictoDisponibilidadException.class,
                    () -> disponibilidadService.validarYLanzarSiHayConflictos(
                            1L, Collections.emptyList(), fechaInicio, fechaFin)
            );

            assertNotNull(exception.getResultadoValidacion());
            assertFalse(exception.getResultadoValidacion().isDisponible());
            assertTrue(exception.getMessage().contains("Conflicto de disponibilidad"));
        }

        @Test
        @DisplayName("No lanza excepción cuando no hay conflictos")
        void validarYLanzar_SinConflictos_NoLanzaExcepcion() {
            // Arrange
            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());
            when(vueloRepository.findVuelosEnRangoPorTripulante(anyLong(), any(), any(), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act & Assert - No debe lanzar excepción
            assertDoesNotThrow(() -> disponibilidadService.validarYLanzarSiHayConflictos(
                    1L, List.of(1L), fechaInicio, fechaFin));
        }
    }

    @Nested
    @DisplayName("Casos Borde Tests")
    class CasosBordeTests {

        @Test
        @DisplayName("Vuelos consecutivos sin solapamiento no generan conflicto")
        void vuelosConsecutivos_SinSolapamiento_NoGeneranConflicto() {
            // Arrange - Vuelo existente termina a las 14:00, nuevo vuelo inicia a las 14:00
            LocalDateTime nuevoInicio = fechaFin; // 14:00
            LocalDateTime nuevoFin = fechaFin.plusHours(2); // 16:00

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(eq(1L), eq(nuevoInicio), eq(nuevoFin), anyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            DisponibilidadAeronaveDTO resultado = disponibilidadService.consultarDisponibilidadAeronave(
                    1L, nuevoInicio, nuevoFin);

            // Assert
            assertTrue(resultado.isDisponible());
        }

        @Test
        @DisplayName("Aeronave no encontrada lanza excepción")
        void consultarDisponibilidad_AeronaveNoExiste_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> disponibilidadService.consultarDisponibilidadAeronave(999L, fechaInicio, fechaFin));
        }

        @Test
        @DisplayName("Tripulante no encontrado lanza excepción")
        void consultarDisponibilidad_TripulanteNoExiste_LanzaExcepcion() {
            // Arrange
            when(tripulanteRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> disponibilidadService.consultarDisponibilidadTripulante(999L, fechaInicio, fechaFin));
        }

        @Test
        @DisplayName("Validar sin aeronave ni tripulantes retorna disponible")
        void validarConflictos_SinRecursos_RetornaDisponible() {
            // Act
            ResultadoValidacionDTO resultado = disponibilidadService.validarConflictosAgenda(
                    null, Collections.emptyList(), fechaInicio, fechaFin);

            // Assert
            assertTrue(resultado.isDisponible());
        }
    }
}
