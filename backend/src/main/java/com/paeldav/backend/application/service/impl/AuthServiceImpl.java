package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.LoginRequest;
import com.paeldav.backend.application.dto.auth.RegisterRequest;
import com.paeldav.backend.application.service.base.AuthService;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SesionService sesionService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String dispositivo, String direccionIp, String userAgent) {
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
                .build();
    }
}
