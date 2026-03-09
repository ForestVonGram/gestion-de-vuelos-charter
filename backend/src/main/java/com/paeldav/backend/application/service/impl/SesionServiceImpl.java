package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.auth.SesionActivaDTO;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.domain.entity.SesionActiva;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.infraestructure.repository.SesionActivaRepository;
import com.paeldav.backend.infraestructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Servicio encargado de gestionar las sesiones activas de los usuarios.
 */
@Service
@RequiredArgsConstructor
public class SesionServiceImpl implements SesionService {

    private final SesionActivaRepository sesionActivaRepository;
    private final JwtService jwtService;

    /**
     * Crea una nueva sesión para un usuario autenticado.
     */
    @Override
    @Transactional
    public SesionActiva crearSesion(Usuario usuario, String token, String dispositivo, String direccionIp, String userAgent) {
        long expirationMs = jwtService.getExpirationTime();
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusSeconds(expirationMs / 1000);

        SesionActiva sesion = SesionActiva.builder()
                .usuario(usuario)
                .tokenHash(hashToken(token))
                .dispositivo(dispositivo)
                .direccionIp(direccionIp)
                .userAgent(userAgent)
                .fechaExpiracion(fechaExpiracion)
                .activa(true)
                .build();

        return sesionActivaRepository.save(sesion);
    }

    /**
     * Obtiene las sesiones activas de un usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SesionActivaDTO> obtenerSesionesActivas(Long usuarioId, String tokenActual) {
        String tokenHashActual = hashToken(tokenActual);
        List<SesionActiva> sesiones = sesionActivaRepository.findByUsuarioIdAndActivaTrue(usuarioId);

        return sesiones.stream()
                .filter(s -> !s.isExpirada())
                .map(s -> SesionActivaDTO.builder()
                        .id(s.getId())
                        .dispositivo(s.getDispositivo())
                        .direccionIp(s.getDireccionIp())
                        .fechaCreacion(s.getFechaCreacion())
                        .fechaExpiracion(s.getFechaExpiracion())
                        .ultimaActividad(s.getUltimaActividad())
                        .sesionActual(s.getTokenHash().equals(tokenHashActual))
                        .build())
                .toList();
    }

    /**
     * Revoca una sesión específica del usuario.
     */
    @Override
    @Transactional
    public void revocarSesion(Long sesionId, Long usuarioId) {
        SesionActiva sesion = sesionActivaRepository.findById(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        if (!sesion.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para revocar esta sesión");
        }

        sesion.setActiva(false);
        sesionActivaRepository.save(sesion);
    }

    /**
     * Revoca todas las sesiones activas de un usuario.
     */
    @Override
    @Transactional
    public void revocarTodasLasSesiones(Long usuarioId) {
        sesionActivaRepository.revocarTodasLasSesiones(usuarioId);
    }

    /**
     * Revoca todas las sesiones excepto la actual.
     */
    @Override
    @Transactional
    public void revocarOtrasSesiones(Long usuarioId, String tokenActual) {
        String tokenHashActual = hashToken(tokenActual);
        List<SesionActiva> sesiones = sesionActivaRepository.findByUsuarioIdAndActivaTrue(usuarioId);

        sesiones.stream()
                .filter(s -> !s.getTokenHash().equals(tokenHashActual))
                .forEach(s -> {
                    s.setActiva(false);
                    sesionActivaRepository.save(s);
                });
    }

    /**
     * Valida si una sesión sigue siendo válida.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean validarSesion(String token) {
        String tokenHash = hashToken(token);
        return sesionActivaRepository.findValidSession(tokenHash, LocalDateTime.now()).isPresent();
    }

    /**
     * Actualiza la última actividad de una sesión.
     */
    @Override
    @Transactional
    public void actualizarUltimaActividad(String token) {
        String tokenHash = hashToken(token);
        sesionActivaRepository.actualizarUltimaActividad(tokenHash, LocalDateTime.now());
    }

    /**
     * Genera un hash SHA-256 del token para almacenarlo de forma segura.
     */
    @Override
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear token", e);
        }
    }
}