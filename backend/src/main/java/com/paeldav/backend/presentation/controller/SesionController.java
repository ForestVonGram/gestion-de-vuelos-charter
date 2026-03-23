package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.auth.SesionActivaDTO;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Define este componente como controlador REST
@RequestMapping("/api/sesiones") // Ruta base para los endpoints de sesiones
@RequiredArgsConstructor // Genera constructor con dependencias finales
public class SesionController {

    private final SesionService sesionService; // Servicio encargado de la lógica de sesiones
    private final UsuarioRepository usuarioRepository; // Repositorio para acceder a usuarios

    @GetMapping
    public ResponseEntity<List<SesionActivaDTO>> obtenerMisSesiones(HttpServletRequest request) {
        // Obtiene el usuario autenticado actualmente
        Usuario usuario = obtenerUsuarioActual();

        // Extrae el token JWT de la petición
        String token = extraerToken(request);

        // Consulta las sesiones activas del usuario
        List<SesionActivaDTO> sesiones = sesionService.obtenerSesionesActivas(usuario.getId(), token);

        return ResponseEntity.ok(sesiones);
    }

    @DeleteMapping("/{sesionId}")
    public ResponseEntity<Void> revocarSesion(@PathVariable Long sesionId) {
        // Obtiene el usuario autenticado
        Usuario usuario = obtenerUsuarioActual();

        // Revoca una sesión específica del usuario
        sesionService.revocarSesion(sesionId, usuario.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/otras")
    public ResponseEntity<Void> revocarOtrasSesiones(HttpServletRequest request) {
        // Obtiene el usuario actual
        Usuario usuario = obtenerUsuarioActual();

        // Extrae el token de la sesión actual
        String token = extraerToken(request);

        // Revoca todas las sesiones excepto la actual
        sesionService.revocarOtrasSesiones(usuario.getId(), token);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/todas")
    public ResponseEntity<Void> revocarTodasLasSesiones() {
        // Obtiene el usuario actual
        Usuario usuario = obtenerUsuarioActual();

        // Revoca todas las sesiones activas del usuario
        sesionService.revocarTodasLasSesiones(usuario.getId());

        return ResponseEntity.ok().build();
    }

    private Usuario obtenerUsuarioActual() {
        // Obtiene la autenticación desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extrae el email del usuario autenticado
        String email = authentication.getName();

        // Busca el usuario en la base de datos
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    private String extraerToken(HttpServletRequest request) {
        // Obtiene el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Extrae el token si tiene el prefijo Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Lanza excepción si no se proporciona token
        throw new IllegalArgumentException("Token no proporcionado");
    }
}