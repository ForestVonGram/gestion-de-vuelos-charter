package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.auth.ChangePasswordRequest;
import com.paeldav.backend.application.dto.auth.ForgotPasswordRequest;
import com.paeldav.backend.application.dto.auth.ResetPasswordRequest;
import com.paeldav.backend.application.service.base.PasswordService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Solicita recuperación de contraseña (envía email con token)
     * Endpoint público
     */
    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.solicitarRecuperacion(request.getEmail());
        // Siempre retornamos éxito por seguridad (no revelar si el email existe)
        return ResponseEntity.ok(Map.of(
                "mensaje", "Si el email está registrado, recibirás un enlace para restablecer tu contraseña"
        ));
    }

    /**
     * Valida si un token de recuperación es válido
     * Endpoint público
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
        boolean valido = passwordService.validarToken(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    /**
     * Resetea la contraseña usando el token de recuperación
     * Endpoint público
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetearPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
    }

    /**
     * Cambia la contraseña del usuario autenticado
     * Endpoint protegido
     */
    @PostMapping("/change")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Usuario usuario = obtenerUsuarioActual();
        passwordService.cambiarPassword(usuario.getId(), request.getPasswordActual(), request.getNuevaPassword());
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
