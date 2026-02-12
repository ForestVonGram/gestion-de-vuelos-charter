package com.paeldav.backend.infraestructure.exception;

import com.paeldav.backend.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests del Manejador Global de Excepciones")
class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler exceptionHandler;
    private ServletWebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
        webRequest = new ServletWebRequest(mockRequest);
    }

    @Test
    @DisplayName("Debe manejar UsuarioNoEncontradoException correctamente")
    void testHandleUsuarioNoEncontrado() {
        UsuarioNoEncontradoException exception = new UsuarioNoEncontradoException("Usuario con ID 1 no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUsuarioNoEncontrado(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("USUARIO_NOT_FOUND", response.getBody().getCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar VueloNoEncontradoException correctamente")
    void testHandleVueloNoEncontrado() {
        VueloNoEncontradoException exception = new VueloNoEncontradoException("Vuelo no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleVueloNoEncontrado(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("VUELO_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar UsuarioYaExisteException correctamente")
    void testHandleUsuarioYaExiste() {
        UsuarioYaExisteException exception = new UsuarioYaExisteException("El usuario ya existe");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUsuarioYaExiste(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("USUARIO_ALREADY_EXISTS", response.getBody().getCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar AuthorizationException correctamente")
    void testHandleAuthorizationException() {
        AuthorizationException exception = new AuthorizationException("No tiene permisos");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthorizationException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("AUTHORIZATION_ERROR", response.getBody().getCode());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar excepciones generales correctamente")
    void testHandleGeneralException() {
        Exception exception = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    @DisplayName("ErrorResponse debe incluir timestamp")
    void testErrorResponseIncludesTimestamp() {
        ErrorResponse errorResponse = new ErrorResponse("TEST_ERROR", "Mensaje de prueba", 400);

        assertNotNull(errorResponse.getTimestamp());
        assertEquals("TEST_ERROR", errorResponse.getCode());
        assertEquals(400, errorResponse.getStatus());
    }

    @Test
    @DisplayName("Debe manejar AeronaveNoEncontradaException correctamente")
    void testHandleAeronaveNoEncontrada() {
        AeronaveNoEncontradaException exception = new AeronaveNoEncontradaException("Aeronave no encontrada");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAeronaveNoEncontrada(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("AERONAVE_NOT_FOUND", response.getBody().getCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar TripulanteNoEncontradoException correctamente")
    void testHandleTripulanteNoEncontrado() {
        TripulanteNoEncontradoException exception = new TripulanteNoEncontradoException("Tripulante no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTripulanteNoEncontrado(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("TRIPULANTE_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar PersonalNoEncontradoException correctamente")
    void testHandlePersonalNoEncontrado() {
        PersonalNoEncontradoException exception = new PersonalNoEncontradoException("Personal no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePersonalNoEncontrado(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("PERSONAL_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar AeronaveYaExisteException correctamente")
    void testHandleAeronaveYaExiste() {
        AeronaveYaExisteException exception = new AeronaveYaExisteException("La aeronave ya existe");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAeronaveYaExiste(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("AERONAVE_ALREADY_EXISTS", response.getBody().getCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar VueloEstadoInvalidoException correctamente")
    void testHandleVueloEstadoInvalido() {
        VueloEstadoInvalidoException exception = new VueloEstadoInvalidoException("Estado de vuelo inválido");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleVueloEstadoInvalido(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VUELO_ESTADO_INVALIDO", response.getBody().getCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar ConflictoDisponibilidadException correctamente")
    void testHandleConflictoDisponibilidad() {
        ConflictoDisponibilidadException exception = new ConflictoDisponibilidadException("Conflicto de disponibilidad");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConflictoDisponibilidad(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICTO_DISPONIBILIDAD", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar CertificacionVencidaException correctamente")
    void testHandleCertificacionVencida() {
        CertificacionVencidaException exception = new CertificacionVencidaException("Certificación vencida");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCertificacionVencida(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CERTIFICACION_VENCIDA", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar CapacidadInsuficienteException correctamente")
    void testHandleCapacidadInsuficiente() {
        CapacidadInsuficienteException exception = new CapacidadInsuficienteException("Capacidad insuficiente");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCapacidadInsuficiente(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CAPACIDAD_INSUFICIENTE", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con múltiples errores")
    void testHandleValidationExceptionMultipleErrors() {
        MapBindingResult bindingResult = new MapBindingResult(java.util.Collections.emptyMap(), "test");
        bindingResult.addError(new FieldError("test", "field1", "", false, null, null, "Campo 1 requerido"));
        bindingResult.addError(new FieldError("test", "field2", "", false, null, null, "Campo 2 requerido"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult);

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals(2, response.getBody().getFieldErrors().size());
    }

    @Test
    @DisplayName("ValidationErrorResponse debe contener detalles de campos")
    void testValidationErrorResponseStructure() {
        java.util.List<FieldErrorDetail> fieldErrors = java.util.List.of(
                FieldErrorDetail.builder()
                        .field("email")
                        .message("Email es requerido")
                        .rejectedValue(null)
                        .build()
        );

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Error de validación")
                .fieldErrors(fieldErrors)
                .status(400)
                .build();

        assertEquals("VALIDATION_ERROR", response.getCode());
        assertEquals(1, response.getFieldErrors().size());
        assertEquals("email", response.getFieldErrors().get(0).getField());
    }

    @Test
    @DisplayName("ErrorResponse debe incluir path en la respuesta")
    void testErrorResponseIncludesPath() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TEST_ERROR")
                .message("Mensaje de prueba")
                .status(400)
                .path("/api/test")
                .build();

        assertNotNull(errorResponse.getPath());
        assertEquals("/api/test", errorResponse.getPath());
    }

    @Test
    @DisplayName("Debe manejar MantenimientoNoEncontradoException correctamente")
    void testHandleMantenimientoNoEncontrado() {
        MantenimientoNoEncontradoException exception = new MantenimientoNoEncontradoException("Mantenimiento no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMantenimientoNoEncontrado(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("MANTENIMIENTO_NOT_FOUND", response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar RepostajeNoEncontradoException correctamente")
    void testHandleRepostajeNoEncontrado() {
        RepostajeNoEncontradoException exception = new RepostajeNoEncontradoException("Repostaje no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRepostajeNoEncontrado(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("REPOSTAJE_NOT_FOUND", response.getBody().getCode());
    }
}
