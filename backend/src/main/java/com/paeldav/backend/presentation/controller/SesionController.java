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

@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<SesionActivaDTO>> obtenerMisSesiones(HttpServletRequest request) {
        Usuario usuario = obtenerUsuarioActual();
        String token = extraerToken(request);

        List<SesionActivaDTO> sesiones = sesionService.obtenerSesionesActivas(usuario.getId(), token);
        return ResponseEntity.ok(sesiones);
    }

    @DeleteMapping("/{sesionId}")
    public ResponseEntity<Void> revocarSesion(@PathVariable Long sesionId) {
        Usuario usuario = obtenerUsuarioActual();
        sesionService.revocarSesion(sesionId, usuario.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/otras")
    public ResponseEntity<Void> revocarOtrasSesiones(HttpServletRequest request) {
        Usuario usuario = obtenerUsuarioActual();
        String token = extraerToken(request);

        sesionService.revocarOtrasSesiones(usuario.getId(), token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/todas")
    public ResponseEntity<Void> revocarTodasLasSesiones() {
        Usuario usuario = obtenerUsuarioActual();
        sesionService.revocarTodasLasSesiones(usuario.getId());
        return ResponseEntity.ok().build();
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    private String extraerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Token no proporcionado");
    }
}
