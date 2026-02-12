package com.paeldav.backend.infraestructure.exception;

import com.paeldav.backend.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones para la aplicación.
 * Centraliza el manejo de excepciones y genera respuestas consistentes.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ============= Excepciones de Recursos No Encontrados =============

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex, WebRequest request) {
        log.warn("Usuario no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USUARIO_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VueloNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleVueloNoEncontrado(VueloNoEncontradoException ex, WebRequest request) {
        log.warn("Vuelo no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VUELO_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AeronaveNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleAeronaveNoEncontrada(AeronaveNoEncontradaException ex, WebRequest request) {
        log.warn("Aeronave no encontrada: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AERONAVE_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TripulanteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleTripulanteNoEncontrado(TripulanteNoEncontradoException ex, WebRequest request) {
        log.warn("Tripulante no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TRIPULANTE_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonalNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePersonalNoEncontrado(PersonalNoEncontradoException ex, WebRequest request) {
        log.warn("Personal no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("PERSONAL_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MantenimientoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleMantenimientoNoEncontrado(MantenimientoNoEncontradoException ex, WebRequest request) {
        log.warn("Mantenimiento no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("MANTENIMIENTO_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RepostajeNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRepostajeNoEncontrado(RepostajeNoEncontradoException ex, WebRequest request) {
        log.warn("Repostaje no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("REPOSTAJE_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PagoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePagoNoEncontrado(PagoNoEncontradoException ex, WebRequest request) {
        log.warn("Pago no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("PAGO_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlertaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleAlertaNoEncontrada(AlertaNoEncontradaException ex, WebRequest request) {
        log.warn("Alerta no encontrada: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ALERTA_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncidenciaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleIncidenciaNoEncontrada(IncidenciaNoEncontradaException ex, WebRequest request) {
        log.warn("Incidencia no encontrada: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INCIDENCIA_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DocumentoTecnicoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleDocumentoTecnicoNoEncontrado(DocumentoTecnicoNoEncontradoException ex, WebRequest request) {
        log.warn("Documento técnico no encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DOCUMENTO_TECNICO_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // ============= Excepciones de Recursos Ya Existentes =============

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioYaExiste(UsuarioYaExisteException ex, WebRequest request) {
        log.warn("Usuario ya existe: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USUARIO_ALREADY_EXISTS")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AeronaveYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleAeronaveYaExiste(AeronaveYaExisteException ex, WebRequest request) {
        log.warn("Aeronave ya existe: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AERONAVE_ALREADY_EXISTS")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TripulanteYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleTripulanteYaExiste(TripulanteYaExisteException ex, WebRequest request) {
        log.warn("Tripulante ya existe: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TRIPULANTE_ALREADY_EXISTS")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // ============= Excepciones de Validación y Estado =============

    @ExceptionHandler(VueloEstadoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleVueloEstadoInvalido(VueloEstadoInvalidoException ex, WebRequest request) {
        log.warn("Estado de vuelo inválido: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VUELO_ESTADO_INVALIDO")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AsignacionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleAsignacionInvalida(AsignacionInvalidaException ex, WebRequest request) {
        log.warn("Asignación inválida: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ASIGNACION_INVALIDA")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CapacidadInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleCapacidadInsuficiente(CapacidadInsuficienteException ex, WebRequest request) {
        log.warn("Capacidad insuficiente: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CAPACIDAD_INSUFICIENTE")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictoDisponibilidadException.class)
    public ResponseEntity<ErrorResponse> handleConflictoDisponibilidad(ConflictoDisponibilidadException ex, WebRequest request) {
        log.warn("Conflicto de disponibilidad: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CONFLICTO_DISPONIBILIDAD")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AeronaveNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleAeronaveNoDisponible(AeronaveNoDisponibleException ex, WebRequest request) {
        log.warn("Aeronave no disponible: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AERONAVE_NO_DISPONIBLE")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AeronaveNoOperativaException.class)
    public ResponseEntity<ErrorResponse> handleAeronaveNoOperativa(AeronaveNoOperativaException ex, WebRequest request) {
        log.warn("Aeronave no operativa: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AERONAVE_NO_OPERATIVA")
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CertificacionVencidaException.class)
    public ResponseEntity<ErrorResponse> handleCertificacionVencida(CertificacionVencidaException ex, WebRequest request) {
        log.warn("Certificación vencida: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CERTIFICACION_VENCIDA")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequisitoTecnicoNoMetException.class)
    public ResponseEntity<ErrorResponse> handleRequisitoTecnicoNoMet(RequisitoTecnicoNoMetException ex, WebRequest request) {
        log.warn("Requisito técnico no cumplido: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("REQUISITO_TECNICO_NO_MET")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CargaArchivoException.class)
    public ResponseEntity<ErrorResponse> handleCargaArchivo(CargaArchivoException ex, WebRequest request) {
        log.warn("Error en carga de archivo: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CARGA_ARCHIVO_ERROR")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ============= Excepciones de Autorización y Seguridad =============

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException ex, WebRequest request) {
        log.warn("Error de autorización: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AUTHORIZATION_ERROR")
                .message(ex.getMessage())
                .status(HttpStatus.FORBIDDEN.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // ============= Excepciones de Validación de Bean =============

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Error de validación en argumentos de método");
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                FieldErrorDetail errorDetail = FieldErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build();
                fieldErrors.add(errorDetail);
            }
        });

        ValidationErrorResponse validationResponse = ValidationErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Error de validación en los datos de entrada")
                .fieldErrors(fieldErrors)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
    }

    // ============= Excepción General =============

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        log.error("Error inesperado en la aplicación", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("Ha ocurrido un error interno en el servidor")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
