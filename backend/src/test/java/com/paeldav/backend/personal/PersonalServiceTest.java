package com.paeldav.backend.personal;

import com.paeldav.backend.application.dto.personal.PersonalCreateDTO;
import com.paeldav.backend.application.dto.personal.PersonalDTO;
import com.paeldav.backend.application.dto.personal.PersonalUpdateDTO;
import com.paeldav.backend.application.mapper.PersonalMapper;
import com.paeldav.backend.application.service.impl.PersonalServiceImpl;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.PersonalNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.PersonalRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonalService Tests")
class PersonalServiceTest {

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PersonalMapper personalMapper;

    @InjectMocks
    private PersonalServiceImpl personalService;

    private PersonalCreateDTO personalCreateDTO;
    private Personal personal;
    private PersonalDTO personalDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Rodriguez")
                .email("juan@example.com")
                .telefono("987654321")
                .rol(RolUsuario.OPERADOR_LOGISTICA)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        personalCreateDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP-001")
                .cargo(CargoPersonal.MECANICO)
                .areaEspecializacion("Motores")
                .certificaciones("FAA, EASA")
                .fechaContratacion(LocalDate.of(2020, 1, 15))
                .turno("Diurno")
                .observaciones("Mecanico experimentado")
                .build();

        personal = Personal.builder()
                .id(1L)
                .usuario(usuario)
                .numeroEmpleado("EMP-001")
                .cargo(CargoPersonal.MECANICO)
                .estado(EstadoPersonal.ACTIVO)
                .areaEspecializacion("Motores")
                .certificaciones("FAA, EASA")
                .fechaContratacion(LocalDate.of(2020, 1, 15))
                .turno("Diurno")
                .observaciones("Mecanico experimentado")
                .build();

        personalDTO = PersonalDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Rodriguez")
                .usuarioEmail("juan@example.com")
                .numeroEmpleado("EMP-001")
                .cargo(CargoPersonal.MECANICO)
                .estado(EstadoPersonal.ACTIVO)
                .areaEspecializacion("Motores")
                .certificaciones("FAA, EASA")
                .fechaContratacion(LocalDate.of(2020, 1, 15))
                .turno("Diurno")
                .observaciones("Mecanico experimentado")
                .build();
    }

    @Nested
    @DisplayName("Registrar Personal Tests")
    class RegistrarPersonalTests {

        @Test
        @DisplayName("Registrar personal exitosamente")
        void registrarPersonal_ConDatosValidos_CreaNuevoPersonal() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(personalRepository.existsByNumeroEmpleado("EMP-001")).thenReturn(false);
            when(personalMapper.toEntity(personalCreateDTO)).thenReturn(personal);
            when(personalRepository.save(any(Personal.class))).thenReturn(personal);
            when(personalMapper.toDTO(personal)).thenReturn(personalDTO);

            PersonalDTO resultado = personalService.registrarPersonal(personalCreateDTO);

            assertNotNull(resultado);
            assertEquals("EMP-001", resultado.getNumeroEmpleado());
            assertEquals(CargoPersonal.MECANICO, resultado.getCargo());
            verify(usuarioRepository).findById(1L);
            verify(personalRepository).existsByNumeroEmpleado("EMP-001");
            verify(personalRepository).save(any(Personal.class));
        }

        @Test
        @DisplayName("No permitir registrar con numero empleado duplicado")
        void registrarPersonal_ConNumeroEmpleadoDuplicado_LanzaExcepcion() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(personalRepository.existsByNumeroEmpleado("EMP-001")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> {
                personalService.registrarPersonal(personalCreateDTO);
            });

            verify(personalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Obtener Personal Tests")
    class ObtenerPersonalTests {

        @Test
        @DisplayName("Obtener personal por ID exitosamente")
        void obtenerPersonalPorId_PersonalExiste_RetornaDTO() {
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalMapper.toDTO(personal)).thenReturn(personalDTO);

            PersonalDTO resultado = personalService.obtenerPersonalPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(personalRepository).findById(1L);
        }

        @Test
        @DisplayName("Lanzar excepcion cuando personal no existe")
        void obtenerPersonalPorId_NoExiste_LanzaExcepcion() {
            when(personalRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(PersonalNoEncontradoException.class, () -> {
                personalService.obtenerPersonalPorId(1L);
            });
        }

        @Test
        @DisplayName("Obtener personal por numero empleado")
        void obtenerPersonalPorNumeroEmpleado_ExisteConNumero_RetornaDTO() {
            when(personalRepository.findByNumeroEmpleado("EMP-001")).thenReturn(Optional.of(personal));
            when(personalMapper.toDTO(personal)).thenReturn(personalDTO);

            PersonalDTO resultado = personalService.obtenerPersonalPorNumeroEmpleado("EMP-001");

            assertNotNull(resultado);
            assertEquals("EMP-001", resultado.getNumeroEmpleado());
        }
    }

    @Nested
    @DisplayName("Editar Personal Tests")
    class EditarPersonalTests {

        @Test
        @DisplayName("Editar personal exitosamente")
        void editarPersonal_ConDatosValidos_ActualizaPersonal() {
        PersonalUpdateDTO updateDTO = PersonalUpdateDTO.builder()
                    .cargo(CargoPersonal.TECNICO_COMBUSTIBLE)
                    .areaEspecializacion("Sistemas Electronicos")
                    .build();

            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            doNothing().when(personalMapper).updateEntityFromUpdateDTO(updateDTO, personal);
            when(personalRepository.save(personal)).thenReturn(personal);
            when(personalMapper.toDTO(personal)).thenReturn(personalDTO);

            PersonalDTO resultado = personalService.editarPersonal(1L, updateDTO);

            assertNotNull(resultado);
            verify(personalRepository).findById(1L);
            verify(personalRepository).save(personal);
        }
    }

    @Nested
    @DisplayName("Cambiar Estado Tests")
    class CambiarEstadoTests {

        @Test
        @DisplayName("Desactivar personal exitosamente")
        void desactivarPersonal_PersonalExiste_DesactivaPersonal() {
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalRepository.save(personal)).thenReturn(personal);

            personalService.desactivarPersonal(1L);

            assertEquals(EstadoPersonal.INACTIVO, personal.getEstado());
            verify(personalRepository).save(personal);
        }

        @Test
        @DisplayName("Activar personal exitosamente")
        void activarPersonal_PersonalInactivo_ActivaPersonal() {
            personal.setEstado(EstadoPersonal.INACTIVO);
            when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
            when(personalRepository.save(personal)).thenReturn(personal);

            personalService.activarPersonal(1L);

            assertEquals(EstadoPersonal.ACTIVO, personal.getEstado());
            verify(personalRepository).save(personal);
        }
    }

    @Nested
    @DisplayName("Obtener Todo Personal Tests")
    class ObtenerTodoPersonalTests {

        @Test
        @DisplayName("Obtener todo el personal registrado")
        void obtenerTodoPersonal_RetornaLista() {
            List<Personal> personalList = List.of(personal);
            when(personalRepository.findAll()).thenReturn(personalList);
            when(personalMapper.toDTOList(personalList)).thenReturn(List.of(personalDTO));

            List<PersonalDTO> resultado = personalService.obtenerTodoPersonal();

            assertEquals(1, resultado.size());
            verify(personalRepository).findAll();
        }
    }
}
