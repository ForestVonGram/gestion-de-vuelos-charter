package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.service.base.PasswordService;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.application.service.integration.EmailServiceImpl;
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
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailServiceImpl;
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

        // Generar UUID para el token y código de 6 dígitos
        String token = UUID.randomUUID().toString();
        String codigo = generarCodigo6Digitos();

        // Crear nuevo token con código incluido
        TokenRecuperacion tokenRecuperacion = TokenRecuperacion.builder()
                .usuario(usuario)
                .token(token)
                .codigo(codigo)
                .fechaExpiracion(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS))
                .usado(false)
                .build();

        tokenRecuperacionRepository.save(tokenRecuperacion);

        // Enviar email con el código de 6 dígitos
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        emailServiceImpl.enviarEmailRecuperacionConCodigo(usuario.getEmail(), codigo, nombreCompleto);

        log.info("Token y código de recuperación generado para usuario: {}", usuario.getId());
    }

    private String generarCodigo6Digitos() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000); // Genera número entre 100000 y 999999
        return String.valueOf(codigo);
    }

    @Override
    @Transactional
    public String verificarCodigoYGenerarToken(String email, String codigo) {
        // Buscar token por código y email
        TokenRecuperacion tokenRecuperacion = tokenRecuperacionRepository
                .findValidTokenByCodigoAndEmail(codigo, email, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Código inválido o expirado"));

        // Retornar el token UUID asociado para el siguiente paso
        return tokenRecuperacion.getToken();
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
        emailServiceImpl.enviarEmailConfirmacionCambio(usuario.getEmail(), nombreCompleto);

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
        emailServiceImpl.enviarEmailConfirmacionCambio(usuario.getEmail(), nombreCompleto);

        log.info("Contraseña cambiada para usuario: {}", usuario.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarToken(String token) {
        return tokenRecuperacionRepository.findValidToken(token, LocalDateTime.now()).isPresent();
    }
}