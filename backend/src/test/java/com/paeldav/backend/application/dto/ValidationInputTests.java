package com.paeldav.backend.application.dto;

import com.paeldav.backend.application.dto.usuario.UsuarioCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.domain.enums.RolUsuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Validación de Entradas Inválidas")
class ValidationInputTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ============= Tests para Usuario =============

    @Test
    @DisplayName("Debe rechazar usuario con email inválido")
    void testCrearUsuarioConEmailInvalido() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("email-invalido")
                .password("password123")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar email inválido");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Debe rechazar usuario con contraseña muy corta")
    void testCrearUsuarioConPasswordCorta() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .password("123")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar contraseña muy corta");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Debe rechazar usuario con nombre nulo")
    void testCrearUsuarioConNombreNulo() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre(null)
                .apellido("Pérez")
                .email("juan@example.com")
                .password("password123")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar nombre nulo");
    }

    @Test
    @DisplayName("Debe rechazar usuario con apellido nulo")
    void testCrearUsuarioConApellidoNulo() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido(null)
                .email("juan@example.com")
                .password("password123")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar apellido nulo");
    }

    @Test
    @DisplayName("Debe rechazar usuario con email nulo")
    void testCrearUsuarioConEmailNulo() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email(null)
                .password("password123")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar email nulo");
    }

    @Test
    @DisplayName("Debe rechazar usuario con contraseña nula")
    void testCrearUsuarioConPasswordNula() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .password(null)
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar contraseña nula");
    }

    @Test
    @DisplayName("Debe aceptar usuario válido")
    void testCrearUsuarioValido() {
        UsuarioCreateDTO usuarioDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .password("password123")
                .rol(RolUsuario.ADMINISTRADOR)
                .build();

        Set<ConstraintViolation<UsuarioCreateDTO>> violations = validator.validate(usuarioDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar usuario válido");
    }

    // ============= Tests para Vuelo =============

    @Test
    @DisplayName("Debe rechazar vuelo con número de pasajeros superior a 500")
    void testCrearVueloConPasajerosSuperiorAlimite() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Madrid")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(600)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar pasajeros superior a 500");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numeroPasajeros")));
    }

    @Test
    @DisplayName("Debe rechazar vuelo con número de pasajeros negativo")
    void testCrearVueloConPasajerosNegativos() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Madrid")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(-5)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar pasajeros negativos");
    }

    @Test
    @DisplayName("Debe rechazar vuelo con origen nulo")
    void testCrearVueloConOrigenNulo() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen(null)
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(100)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar origen nulo");
    }

    @Test
    @DisplayName("Debe rechazar vuelo con destino nulo")
    void testCrearVueloConDestinoNulo() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Madrid")
                .destino(null)
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(100)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar destino nulo");
    }

    @Test
    @DisplayName("Debe rechazar vuelo con fecha de salida en el pasado")
    void testCrearVueloConFechaSalidaPasada() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Madrid")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().minusHours(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(100)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar fecha de salida en el pasado");
    }

    @Test
    @DisplayName("Debe rechazar vuelo con origen muy corto")
    void testCrearVueloConOrigenCorto() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("M")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(100)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe rechazar origen muy corto");
    }

    @Test
    @DisplayName("Debe aceptar vuelo válido")
    void testCrearVueloValido() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Madrid")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(100)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar vuelo válido");
    }

    @Test
    @DisplayName("Debe aceptar vuelo con propósito opcional")
    void testCrearVueloConProposito() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Madrid")
                .destino("Barcelona")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(100)
                .proposito("Viaje de negocios")
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar vuelo con propósito");
    }
}
