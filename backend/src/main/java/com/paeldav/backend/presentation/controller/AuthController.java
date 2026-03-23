package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.auth.*;
import com.paeldav.backend.application.service.base.AuthService;
import com.paeldav.backend.application.service.base.DosFactoresService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para la gestión de autenticación y seguridad de cuentas.
 * Expone endpoints para login, registro, logout y configuración de doble factor (2FA).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final DosFactoresService dosFactoresService;

    /**
     * Procesa el inicio de sesión de un usuario.
     * Captura metadatos del dispositivo e IP para auditoría y seguridad.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        log.debug("Recibida petición de login para email: {}", request.getEmail());

        try {
            // Extracción de metadatos de la cabecera HTTP para trazabilidad
            String dispositivo = extraerDispositivo(httpRequest);
            String direccionIp = extraerDireccionIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            AuthResponse response = authService.login(request, dispositivo, direccionIp, userAgent);
            log.info("Login exitoso para: {}", request.getEmail());
            log.info("dispositivo: {}", dispositivo);
            log.info("direccionIp: {}", direccionIp);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para: {}", request.getEmail());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Email o contraseña inválidos");
            errorResponse.put("error", "Bad Credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            log.error("Error inesperado en login para {}: {}", request.getEmail(), e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error en el servidor. Por favor intente más tarde.");
            errorResponse.put("error", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Incluye validación de seguridad (reCAPTCHA) y captura de contexto de red.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        log.debug("Recibida petición de registro para email: {}", request.getEmail());

        try {
            String dispositivo = extraerDispositivo(httpRequest);
            String direccionIp = extraerDireccionIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            AuthResponse response = authService.register(request, dispositivo, direccionIp, userAgent);
            log.info("Registro exitoso para: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error en registro: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (BadCredentialsException e) {
            // Maneja fallos en la validación de reCAPTCHA o tokens de seguridad
            log.warn("Error en validación reCAPTCHA para registro: {}", request.getEmail());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validación de seguridad fallida");
            errorResponse.put("error", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            log.error("Error inesperado en registro para {}: {}", request.getEmail(), e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error en el servidor. Por favor intente más tarde.");
            errorResponse.put("error", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cierra la sesión activa invalidando el token JWT proporcionado.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.logout(token);
                log.debug("Sesión cerrada exitosamente");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al cerrar sesión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Valida el código de segundo factor (2FA) para completar el acceso.
     */
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(
            @Valid @RequestBody VerificarCodigoRequest request,
            HttpServletRequest httpRequest) {

        log.debug("Recibida petición de verificación 2FA");

        try {
            String dispositivo = extraerDispositivo(httpRequest);
            String direccionIp = extraerDireccionIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            AuthResponse response = authService.verificarDosFactores(request, dispositivo, direccionIp, userAgent);
            log.info("2FA verificado exitosamente");
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Código 2FA inválido");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Código de verificación inválido o expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            log.error("Error en verificación 2FA: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error en la verificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Activa la protección 2FA para el usuario autenticado.
     */
    @PostMapping("/enable-2fa")
    public ResponseEntity<?> habilitarDosFactores(
            @Valid @RequestBody ConfiguracionDosFactoresDTO config,
            HttpServletRequest httpRequest) {

        log.debug("Recibida petición para habilitar 2FA");

        try {
            authService.habilitarDosFactores(config);
            log.info("2FA habilitado exitosamente");
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.warn("Error al habilitar 2FA: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            log.error("Error al habilitar 2FA: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al habilitar 2FA");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Desactiva la protección 2FA de la cuenta.
     */
    @PostMapping("/disable-2fa")
    public ResponseEntity<?> deshabilitarDosFactores() {

        log.debug("Recibida petición para deshabilitar 2FA");

        try {
            authService.deshabilitarDosFactores();
            log.info("2FA deshabilitado exitosamente");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error al deshabilitar 2FA: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al deshabilitar 2FA");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Consulta si el usuario actual tiene el 2FA activo y qué método utiliza.
     */
    @GetMapping("/2fa-status")
    public ResponseEntity<?> obtenerEstadoDosFactores() {

        log.debug("Recibida petición para obtener estado 2FA");

        try {
            EstadoDosFactoresDTO estado = authService.obtenerEstadoDosFactores();
            return ResponseEntity.ok(estado);

        } catch (Exception e) {
            log.error("Error al obtener estado 2FA: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al obtener estado 2FA");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Identifica el tipo de dispositivo basándose en el User-Agent.
     */
    private String extraerDispositivo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Desconocido";

        if (userAgent.contains("Mobile")) return "Móvil";
        if (userAgent.contains("Tablet")) return "Tablet";
        return "Escritorio";
    }

    /**
     * Obtiene la dirección IP real, considerando posibles proxies o balanceadores (cabecera X-Forwarded-For).
     */
    private String extraerDireccionIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}