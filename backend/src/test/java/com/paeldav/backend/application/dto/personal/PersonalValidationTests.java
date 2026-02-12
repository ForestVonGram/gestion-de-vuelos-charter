package com.paeldav.backend.application.dto.personal;

import com.paeldav.backend.domain.enums.CargoPersonal;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de Validación para PersonalCreateDTO")
class PersonalValidationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe validar un personal válido sin errores")
    void testValidPersonalCreation() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .areaEspecializacion("Aviación")
                .certificaciones("ATP, Commercial")
                .fechaContratacion(LocalDate.of(2020, 1, 15))
                .turno("Matutino")
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "No debe haber violaciones de validación");
    }

    @Test
    @DisplayName("Debe fallar si el usuario ID es nulo")
    void testNullUsuarioId() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(null)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por usuario ID nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("usuarioId")));
    }

    @Test
    @DisplayName("Debe fallar si el número de empleado es nulo")
    void testNullNumeroEmpleado() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado(null)
                .cargo(CargoPersonal.MECANICO)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por número de empleado nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numeroEmpleado")));
    }

    @Test
    @DisplayName("Debe fallar si el número de empleado está vacío")
    void testEmptyNumeroEmpleado() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("")
                .cargo(CargoPersonal.MECANICO)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por número de empleado vacío");
    }

    @Test
    @DisplayName("Debe fallar si el cargo es nulo")
    void testNullCargo() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(null)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertFalse(violations.isEmpty(), "Debe haber violación por cargo nulo");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cargo")));
    }

    @Test
    @DisplayName("Debe validar todos los cargos posibles")
    void testAllCargoTypes() {
        for (CargoPersonal cargo : CargoPersonal.values()) {
            PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                    .usuarioId(1L)
                    .numeroEmpleado("EMP001")
                    .cargo(cargo)
                    .build();

            Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

            assertTrue(violations.isEmpty(), 
                    String.format("Debe aceptar cargo %s", cargo.name()));
        }
    }

    @Test
    @DisplayName("Debe permitir null en campos opcionales")
    void testOptionalFieldsNull() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .areaEspecializacion(null)
                .certificaciones(null)
                .fechaContratacion(null)
                .turno(null)
                .observaciones(null)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Los campos opcionales pueden ser nulos");
    }

    @Test
    @DisplayName("Debe validar con área de especialización")
    void testWithAreaEspecializacion() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .areaEspecializacion("Motores Turbo")
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar área de especialización");
    }

    @Test
    @DisplayName("Debe validar con certificaciones")
    void testWithCertificaciones() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .certificaciones("ATP, Commercial, Multi-Engine")
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar certificaciones");
    }

    @Test
    @DisplayName("Debe validar con fecha de contratación válida")
    void testValidFechaContratacion() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .fechaContratacion(LocalDate.now().minusYears(5))
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar fecha de contratación en el pasado");
    }

    @Test
    @DisplayName("Debe validar con turno")
    void testWithTurno() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .turno("Nocturno")
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar turno");
    }

    @Test
    @DisplayName("Debe validar con observaciones")
    void testWithObservaciones() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .observaciones("Piloto experimentado con 10000 horas de vuelo")
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar observaciones");
    }

    @Test
    @DisplayName("Debe validar usuarioId positivo")
    void testPositiveUsuarioId() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(1L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar usuarioId positivo");
    }

    @Test
    @DisplayName("Debe validar con usuarioId grande")
    void testLargeUsuarioId() {
        PersonalCreateDTO personalDTO = PersonalCreateDTO.builder()
                .usuarioId(999999999L)
                .numeroEmpleado("EMP001")
                .cargo(CargoPersonal.MECANICO)
                .build();

        Set<ConstraintViolation<PersonalCreateDTO>> violations = validator.validate(personalDTO);

        assertTrue(violations.isEmpty(), "Debe aceptar usuarioId grande");
    }
}
