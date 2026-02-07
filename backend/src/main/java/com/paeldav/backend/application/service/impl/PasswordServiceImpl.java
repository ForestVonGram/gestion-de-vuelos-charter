package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.service.base.PasswordService;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.application.service.integration.EmailService;
import com.paeldav.backend.domain.entity.TokenRecuperacion;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.infraestructure.repository.TokenRecuperacionRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SesionService sesionService;

    private static final int TOKEN_EXPIRATION_HOURS = 1;

    @Override
    @Transactional
    public void solicitarRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Por seguridad, no revelamos si el email existe o no
        if (usuario == null || !usuario.getActivo()) {
            log.info("Solicitud de recuperación para email no registrado o inactivo: {}", email);
            return;
        }

        // Invalidar tokens anteriores
        tokenRecuperacionRepository.invalidarTokensAnteriores(usuario.getId());

        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        TokenRecuperacion tokenRecuperacion = TokenRecuperacion.builder()
                .usuario(usuario)
                .token(token)
                .fechaExpiracion(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS))
                .build();

        tokenRecuperacionRepository.save(tokenRecuperacion);

        // Enviar email
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        emailService.enviarEmailRecuperacion(usuario.getEmail(), token, nombreCompleto);

        log.info("Token de recuperación generado para usuario: {}", usuario.getId());
    }

    @Override
    @Transactional
    public void resetearPassword(String token, String nuevaPassword) {
        TokenRecuperacion tokenRecuperacion = tokenRecuperacionRepository
                .findValidToken(token, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        Usuario usuario = tokenRecuperacion.getUsuario();

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Marcar token como usado
        tokenRecuperacion.setUsado(true);
        tokenRecuperacionRepository.save(tokenRecuperacion);

        // Revocar todas las sesiones activas por seguridad
        sesionService.revocarTodasLasSesiones(usuario.getId());

        // Enviar email de confirmación
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        emailService.enviarEmailConfirmacionCambio(usuario.getEmail(), nombreCompleto);

        log.info("Contraseña reseteada para usuario: {}", usuario.getId());
    }

    @Override
    @Transactional
    public void cambiarPassword(Long usuarioId, String passwordActual, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Enviar email de confirmación
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        emailService.enviarEmailConfirmacionCambio(usuario.getEmail(), nombreCompleto);

        log.info("Contraseña cambiada para usuario: {}", usuario.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarToken(String token) {
        return tokenRecuperacionRepository.findValidToken(token, LocalDateTime.now()).isPresent();
    }
}
