package com.paeldav.backend.application.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.ConfiguracionDosFactoresDTO;
import com.paeldav.backend.application.dto.auth.EstadoDosFactoresDTO;
import com.paeldav.backend.application.dto.auth.GoogleAuthRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementación del servicio de autenticación.
 * Maneja login, registro, verificación de 2FA y gestión del estado de autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    // Repositorio para operaciones con usuarios en la base de datos
    private final UsuarioRepository usuarioRepository;

    // Codificador de contraseñas
    private final PasswordEncoder passwordEncoder;

    // Servicio para generación y validación de tokens JWT
    private final JwtService jwtService;

    // Administrador de autenticación de Spring Security
    private final AuthenticationManager authenticationManager;

    // Servicio para gestión de sesiones activas
    private final SesionService sesionService;

    // Servicio encargado de la lógica de autenticación de dos factores
    private final DosFactoresService dosFactoresService;

    // Servicio para validación de reCAPTCHA
    private final RecaptchaService recaptchaService;

    /**
     * Realiza el proceso de autenticación del usuario.
     * Incluye validación de reCAPTCHA, autenticación y verificación de 2FA si está habilitado.
     */
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String dispositivo, String direccionIp, String userAgent) {
        log.debug("Intento de login para email: {}", request.getEmail());

        try {

            // Validar reCAPTCHA si está habilitado
            if (recaptchaService.estaHabilitado() && !recaptchaService.validarToken(request.getRecaptchaToken())) {
                log.warn("reCAPTCHA validation failed for email: {}", request.getEmail());
                throw new BadCredentialsException("Validación reCAPTCHA fallida");
            }

            usuarioRepository.findByEmail(request.getEmail()).ifPresent(u -> {
                if (u.getGoogleId() != null && u.getPassword() == null) {
                    log.warn("Usuario {} intentó login con contraseña pero solo tiene cuenta Google", u.getEmail());
                    throw new BadCredentialsException(
                            "Esta cuenta fue creada con Google. Por favor inicia sesión con Google. " +
                                    "Si deseas usar contraseña, configúrala desde tu perfil."
                    );
                }
            });

            // Autenticar usuario con email y contraseña
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );
            } catch (BadCredentialsException e) {
                log.warn("Credenciales inválidas para email: {}", request.getEmail());
                throw new BadCredentialsException("Credenciales inválidas");
            } catch (DisabledException e) {
                log.warn("Usuario deshabilitado: {}", request.getEmail());
                throw new BadCredentialsException("Usuario inactivo");
            } catch (LockedException e) {
                log.warn("Usuario bloqueado: {}", request.getEmail());
                throw new BadCredentialsException("Usuario bloqueado");
            } catch (AuthenticationException e) {
                log.warn("Error de autenticación para {}: {}", request.getEmail(), e.getMessage());
                throw new BadCredentialsException("Error en la autenticación");
            }

            // Obtener usuario autenticado desde la base de datos
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.error("Usuario no encontrado después de autenticación exitosa: {}", request.getEmail());
                        return new BadCredentialsException("Credenciales inválidas");
                    });

            // Verificar si el usuario está activo
            if (!usuario.getActivo()) {
                log.warn("Usuario inactivo intentó login: {}", request.getEmail());
                throw new BadCredentialsException("Usuario inactivo");
            }

            // Verificar si el usuario tiene autenticación de dos factores activa
            if (dosFactoresService.esActivo(usuario)) {
                log.debug("Usuario {} requiere 2FA", usuario.getEmail());

                // Determinar destino del código (email o teléfono)
                String destino = usuario.getMetodoDosFactores().equals(MetodoDosFactores.EMAIL)
                        ? usuario.getEmail()
                        : usuario.getTelefono();

                // Generar código de verificación 2FA
                dosFactoresService.generarCodigoVerificacion(usuario, usuario.getMetodoDosFactores(), destino);

                // Generar token temporal para sesión 2FA
                String sessionToken = generarSessionToken(usuario);

                // Respuesta indicando que se requiere verificación adicional
                return AuthResponse.builder()
                        .requires2FA(true)
                        .sessionToken(sessionToken)
                        .userId(usuario.getId())
                        .email(usuario.getEmail())
                        .build();
            }

            // Crear objeto UserDetails para generar JWT
            UserDetails userDetails = new User(
                    usuario.getEmail(),
                    usuario.getPassword(),
                    Collections.emptyList()
            );

            // Generar token JWT
            String token = jwtService.generateToken(userDetails);

            // Crear sesión activa del usuario
            sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

            log.info("Login exitoso para usuario: {}", usuario.getEmail());

            return buildAuthResponse(usuario, token);

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en login: {}", e.getMessage(), e);
            throw new BadCredentialsException("Error en la autenticación");
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, String dispositivo, String direccionIp, String userAgent) {
        try {

            // Validar reCAPTCHA
            if (recaptchaService.estaHabilitado() && !recaptchaService.validarToken(request.getRecaptchaToken())) {
                throw new BadCredentialsException("Validación reCAPTCHA fallida");
            }

            // Verificar si el email ya está registrado
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }

            // Crear nuevo usuario
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

            // Crear JWT para el nuevo usuario
            UserDetails userDetails = new User(
                    usuario.getEmail(),
                    usuario.getPassword(),
                    Collections.emptyList()
            );

            String token = jwtService.generateToken(userDetails);

            // Crear sesión activa
            sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

            log.info("Usuario registrado exitosamente: {}", usuario.getEmail());

            return buildAuthResponse(usuario, token);

        } catch (IllegalArgumentException e) {
            log.warn("Error en registro: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar usuario");
        }
    }

    /**
     * Autentica (o registra) un usuario usando el ID Token de Google.
     * Valida el token con los servidores de Google, obtiene los claims del usuario
     * y emite un JWT propio del sistema.
     */
    @Override
    @Transactional
    public AuthResponse loginConGoogle(GoogleAuthRequest request, String dispositivo, String direccionIp, String userAgent) {
        try {
            // Verificar el ID Token con los servidores de Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(List.of(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getCredential());
            if (idToken == null) {
                log.warn("ID Token de Google inválido");
                throw new BadCredentialsException("Token de Google inválido");
            }

            Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String nombre = (String) payload.get("given_name");
            String apellido = (String) payload.get("family_name");

            if (nombre == null) nombre = email.split("@")[0];
            if (apellido == null) apellido = "";

            // Buscar usuario existente por googleId o email
            final String emailFinal = email;
            final String nombreFinal = nombre;
            final String apellidoFinal = apellido;
            final String googleIdFinal = googleId;

            Usuario usuario = usuarioRepository.findByGoogleId(googleId)
                    .orElseGet(() -> usuarioRepository.findByEmail(emailFinal)
                            .map(u -> {
                                if (u.getPassword() != null && u.getGoogleId() == null) {
                                    log.info("Vinculando cuenta existente con contraseña con Google para: {}", u.getEmail());
                                }
                                // Vincular cuenta existente con Google
                                u.setGoogleId(googleIdFinal);
                                return usuarioRepository.save(u);
                            })
                            .orElseGet(() -> {
                                // Registrar nuevo usuario desde Google
                                Usuario nuevo = Usuario.builder()
                                        .nombre(nombreFinal)
                                        .apellido(apellidoFinal)
                                        .email(emailFinal)
                                        .googleId(googleIdFinal)
                                        .rol(RolUsuario.USUARIO)
                                        .activo(true)
                                        .dosFactoresHabilitado(false)
                                        .build();
                                return usuarioRepository.save(nuevo);
                            })
                    );

            if (!usuario.getActivo()) {
                throw new BadCredentialsException("Usuario inactivo");
            }

            // Generar JWT propio del sistema
            UserDetails userDetails = new User(
                    usuario.getEmail(),
                    usuario.getPassword() != null ? usuario.getPassword() : "",
                    Collections.emptyList()
            );

            String token = jwtService.generateToken(userDetails);
            sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

            log.info("Login con Google exitoso para: {}", usuario.getEmail());
            return buildAuthResponse(usuario, token);

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en login con Google: {}", e.getMessage(), e);
            throw new BadCredentialsException("Error en la autenticación con Google");
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    @Override
    @Transactional
    public void logout(String token) {
        try {
            // Generar hash del token para buscar la sesión
            String tokenHash = sesionService.hashToken(token);

            // Aquí se podría implementar la invalidación de la sesión
            log.debug("Sesión cerrada para token: {}", tokenHash);
        } catch (Exception e) {
            log.error("Error al cerrar sesión: {}", e.getMessage());
        }
    }

    /**
     * Construye la respuesta de autenticación estándar.
     */
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

        // Indica que el token es para verificación 2FA
        extraClaims.put("tipo", "2fa-session");
        extraClaims.put("usuarioId", usuario.getId());

        // Generar token con duración de 5 minutos
        return jwtService.generateToken(extraClaims,
                new User(usuario.getEmail(), usuario.getPassword(), Collections.emptyList()),
                300000);
    }

    /**
     * Verifica el código de autenticación de dos factores.
     */
    @Override
    @Transactional
    public AuthResponse verificarDosFactores(VerificarCodigoRequest request, String dispositivo, String direccionIp, String userAgent) {
        try {

            // Verificar código generado previamente
            var verificacion = dosFactoresService.verificarCodigo(request.getCodigo());

            Usuario usuario = verificacion.getUsuario();

            // Marcar el código como verificado
            dosFactoresService.marcarComoVerificado(verificacion);

            // Generar token JWT definitivo
            UserDetails userDetails = new User(
                    usuario.getEmail(),
                    usuario.getPassword(),
                    Collections.emptyList()
            );

            String token = jwtService.generateToken(userDetails);

            // Crear sesión activa
            sesionService.crearSesion(usuario, token, dispositivo, direccionIp, userAgent);

            log.info("2FA verificado exitosamente para usuario: {}", usuario.getEmail());

            return buildAuthResponse(usuario, token);

        } catch (NoSuchElementException e) {
            log.warn("Intento de 2FA con código inválido");
            throw new BadCredentialsException("Código de verificación inválido o expirado");
        } catch (Exception e) {
            log.error("Error en verificación 2FA: {}", e.getMessage(), e);
            throw new BadCredentialsException("Error en la verificación");
        }
    }

    /**
     * Habilita la autenticación de dos factores para el usuario actual.
     */
    @Override
    @Transactional
    public void habilitarDosFactores(ConfiguracionDosFactoresDTO config) {
        try {
            Usuario usuario = obtenerUsuarioActual();

            if (config.getHabilitado() && config.getMetodo() != null) {

                // Determinar destino de envío del código
                String destino = config.getDestino() != null ? config.getDestino() : usuario.getEmail();

                dosFactoresService.habilitarDosFactores(usuario, config.getMetodo(), destino);

                log.info("2FA habilitado para usuario: {}", usuario.getEmail());
            } else {
                throw new IllegalArgumentException("Debe proporcionar método y destino para habilitar 2FA");
            }
        } catch (Exception e) {
            log.error("Error al habilitar 2FA: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Deshabilita la autenticación de dos factores del usuario actual.
     */
    @Override
    @Transactional
    public void deshabilitarDosFactores() {
        try {
            Usuario usuario = obtenerUsuarioActual();

            dosFactoresService.deshabilitarDosFactores(usuario);

            log.info("2FA deshabilitado para usuario: {}", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al deshabilitar 2FA: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el estado actual del 2FA del usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public EstadoDosFactoresDTO obtenerEstadoDosFactores() {
        try {

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

        } catch (Exception e) {
            log.error("Error al obtener estado 2FA: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el usuario autenticado actualmente desde el contexto de seguridad.
     */
    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}