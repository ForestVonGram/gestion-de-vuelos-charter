package com.paeldav.backend.application.dto.vuelo;

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

@DisplayName("Tests de Validación para VueloCreateDTO")
class VueloValidationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe validar un vuelo válido sin errores")
    void testValidVueloCreation() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertTrue(violations.isEmpty(), "No debe haber violaciones de validación");
    }

    @Test
    @DisplayName("Debe fallar si el usuario ID es nulo")
    void testNullUsuarioId() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(null)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por usuario ID nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("usuarioId")));
    }

    @Test
    @DisplayName("Debe fallar si el origen es nulo")
    void testNullOrigen() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen(null)
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por origen nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("origen")));
    }

    @Test
    @DisplayName("Debe fallar si el destino es nulo")
    void testNullDestino() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino(null)
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por destino nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("destino")));
    }

    @Test
    @DisplayName("Debe fallar si la fecha de salida es nula")
    void testNullFechaSalida() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(null)
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por fecha de salida nula");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fechaSalidaProgramada")));
    }

    @Test
    @DisplayName("Debe fallar si la fecha de llegada es nula")
    void testNullFechaLlegada() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(null)
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por fecha de llegada nula");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fechaLlegadaProgramada")));
    }

    @Test
    @DisplayName("Debe fallar si el número de pasajeros es nulo")
    void testNullNumeroPasajeros() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(null)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por número de pasajeros nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numeroPasajeros")));
    }

    @Test
    @DisplayName("Debe fallar si el número de pasajeros es negativo")
    void testNegativeNumeroPasajeros() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(-1)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por número de pasajeros negativo");
    }

    @Test
    @DisplayName("Debe fallar si el número de pasajeros excede el máximo")
    void testExceededMaxNumeroPasajeros() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(501)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por exceso de pasajeros");
    }

    @Test
    @DisplayName("Debe fallar si el origen tiene menos de 2 caracteres")
    void testShortOrigen() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("M")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por origen corto");
    }

    @Test
    @DisplayName("Debe fallar si la fecha de salida es en el pasado")
    void testPastFechaSalida() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().minusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por fecha de salida pasada");
    }

    @Test
    @DisplayName("Debe validar número de pasajeros positivo")
    void testPositiveNumeroPasajeros() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(1)
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar número de pasajeros positivo");
    }

    @Test
    @DisplayName("Debe validar propósito opcional")
    void testOptionalProposito() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .proposito("Viaje de negocios")
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar propósito");
    }

    @Test
    @DisplayName("Debe validar observaciones opcionales")
    void testOptionalObservaciones() {
        VueloCreateDTO vueloDTO = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Miami")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(4))
                .numeroPasajeros(8)
                .observaciones("Vuelo con necesidades especiales")
                .build();

        Set<ConstraintViolation<VueloCreateDTO>> violations = validator.validate(vueloDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar observaciones");
    }
}
