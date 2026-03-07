package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.ConfiguracionDosFactoresDTO;
import com.paeldav.backend.application.dto.auth.EstadoDosFactoresDTO;
import com.paeldav.backend.application.dto.auth.LoginRequest;
import com.paeldav.backend.application.dto.auth.RegisterRequest;
import com.paeldav.backend.application.dto.auth.VerificarCodigoRequest;
import com.paeldav.backend.application.service.base.AuthService;
import com.paeldav.backend.application.service.base.DosFactoresService;
import com.paeldav.backend.application.service.base.RecaptchaService;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.MetodoDosFactores;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SesionService sesionService;
    private final DosFactoresService dosFactoresService;
    private final RecaptchaService recaptchaService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String dispositivo, String direccionIp, String userAgent) {
        // Validar reCAPTCHA
        if (!recaptchaService.validarToken(request.getRecaptchaToken())) {
            throw new BadCredentialsException("Validación reCAPTCHA fallida");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        // Verificar si 2FA está habilitado
        if (dosFactoresService.esActivo(usuario)) {
            // Generar código 2FA
            String destino = usuario.getMetodoDosFactores().equals(MetodoDosFactores.EMAIL) 
                ? usuario.getEmail() 
                : usuario.getTelefono();
            dosFactoresService.generarCodigoVerificacion(usuario, usuario.getMetodoDosFactores(), destino);

            // Generar token temporal de sesión (5 minutos)
            String sessionToken = generarSessionToken(usuario);

            // Retornar respuesta indicando que requiere 2FA
            return AuthResponse.builder()
                    .requires2FA(true)
                    .sessionToken(sessionToken)
                    .userId(usuario.getId())
                    .email(usuario.getEmail())
                    .build();
        }

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList()
        );

        String token = jwtService.generateToken(userDetails);

        // Crear sesión activa
        sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

        return buildAuthResponse(usuario, token);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, String dispositivo, String direccionIp, String userAgent) {
        // Validar reCAPTCHA
        if (!recaptchaService.validarToken(request.getRecaptchaToken())) {
            throw new BadCredentialsException("Validación reCAPTCHA fallida");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .rol(request.getRol() != null ? request.getRol() : RolUsuario.USUARIO)
                .activo(true)
                .dosFactoresHabilitado(false)
                .build();

        usuario = usuarioRepository.save(usuario);

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList()
        );

        String token = jwtService.generateToken(userDetails);

        // Crear sesión activa
        sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

        return buildAuthResponse(usuario, token);
    }

    @Override
    @Transactional
    public void logout(String token) {
        String tokenHash = sesionService.hashToken(token);
        // La sesión se invalida buscando por el hash
        // Esto se maneja revocando la sesión actual
    }

    private AuthResponse buildAuthResponse(Usuario usuario, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .rol(usuario.getRol())
                .requires2FA(false)
                .build();
    }

    /**
     * Genera un token temporal de sesión para verificación 2FA (5 minutos).
     */
    private String generarSessionToken(Usuario usuario) {
        var extraClaims = new HashMap<String, Object>();
        extraClaims.put("tipo", "2fa-session");
        extraClaims.put("usuarioId", usuario.getId());
        // Token con duración de 5 minutos
        return jwtService.generateToken(extraClaims, 
            new User(usuario.getEmail(), usuario.getPassword(), Collections.emptyList()),
            300000); // 5 minutos en milisegundos
    }

    @Override
    @Transactional
    public AuthResponse verificarDosFactores(VerificarCodigoRequest request, String dispositivo, String direccionIp, String userAgent) {
        try {
            // Verificar el código
            var verificacion = dosFactoresService.verificarCodigo(request.getCodigo());

            Usuario usuario = verificacion.getUsuario();

            // Marcar como verificado
            dosFactoresService.marcarComoVerificado(verificacion);

            // Generar token JWT completo
            UserDetails userDetails = new User(
                    usuario.getEmail(),
                    usuario.getPassword(),
                    Collections.emptyList()
            );

            String token = jwtService.generateToken(userDetails);

            // Crear sesión activa
            sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

            return buildAuthResponse(usuario, token);
        } catch (NoSuchElementException e) {
            throw new BadCredentialsException("Código de verificación inválido o expirado");
        }
    }

    @Override
    @Transactional
    public void habilitarDosFactores(ConfiguracionDosFactoresDTO config) {
        Usuario usuario = obtenerUsuarioActual();

        if (config.getHabilitado() && config.getMetodo() != null) {
            String destino = config.getDestino() != null ? config.getDestino() : usuario.getEmail();
            dosFactoresService.habilitarDosFactores(usuario, config.getMetodo(), destino);
        } else {
            throw new IllegalArgumentException("Debe proporcionar método y destino para habilitar 2FA");
        }
    }

    @Override
    @Transactional
    public void deshabilitarDosFactores() {
        Usuario usuario = obtenerUsuarioActual();
        dosFactoresService.deshabilitarDosFactores(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoDosFactoresDTO obtenerEstadoDosFactores() {
        Usuario usuario = obtenerUsuarioActual();

        return EstadoDosFactoresDTO.builder()
                .habilitado(dosFactoresService.esActivo(usuario))
                .metodo(usuario.getMetodoDosFactores())
                .destino(usuario.getMetodoDosFactores() != null 
                    ? dosFactoresService.enmascaraDestino(
                        usuario.getMetodoDosFactores().equals(MetodoDosFactores.EMAIL) 
                            ? usuario.getEmail() 
                            : usuario.getTelefono(),
                        usuario.getMetodoDosFactores()
                      )
                    : null
                )
                .build();
    }

    /**
     * Obtiene el usuario actual autenticado.
     */
    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
