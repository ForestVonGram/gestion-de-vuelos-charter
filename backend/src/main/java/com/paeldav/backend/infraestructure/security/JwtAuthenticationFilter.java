package com.paeldav.backend.infraestructure.security;

import com.paeldav.backend.application.service.base.SesionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Registra el filtro como componente de Spring
@RequiredArgsConstructor // Genera constructor con las dependencias finales
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Servicio para manejar operaciones con JWT
    private final UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario
    private final SesionService sesionService; // Servicio para validar sesiones en base de datos

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Obtiene el header Authorization de la petición
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si no hay token o no empieza con Bearer, continúa con la cadena de filtros
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrae el token JWT eliminando el prefijo "Bearer "
        jwt = authHeader.substring(7);

        try {
            // Extrae el email del usuario desde el token
            userEmail = jwtService.extractUsername(jwt);

            // Si existe email y no hay autenticación previa en el contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Validar que la sesión asociada al token esté activa en base de datos
                if (!sesionService.validarSesion(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Cargar los detalles del usuario desde el sistema
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Validar que el token sea válido para el usuario
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // Crear objeto de autenticación para Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Asociar detalles de la petición al token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Actualizar la última actividad de la sesión
                    sesionService.actualizarUltimaActividad(jwt);
                }
            }
        } catch (Exception e) {
            // Si el token es inválido se continúa sin autenticar al usuario
            logger.debug("Token JWT inválido: " + e.getMessage());
        }

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}