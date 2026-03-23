package com.paeldav.backend.infraestructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación y validación de tokens JWT (JSON Web Tokens).
 * Se encarga de crear tokens de autenticación y verificar su validez.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey; // Clave secreta para firmar los tokens

    @Value("${jwt.expiration}")
    private long jwtExpiration; // Tiempo de expiración del token en milisegundos

    /**
     * Extrae el nombre de usuario (subject) del token.
     * @param token token JWT
     * @return nombre de usuario contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token usando una función de resolución.
     * @param token token JWT
     * @param claimsResolver función que extrae el claim deseado
     * @return valor del claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para un usuario.
     * @param userDetails detalles del usuario autenticado
     * @return token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales.
     * @param extraClaims claims extra a incluir en el token
     * @param userDetails detalles del usuario autenticado
     * @return token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Genera un token JWT con claims adicionales y tiempo de expiración personalizado.
     * @param extraClaims claims extra a incluir en el token
     * @param userDetails detalles del usuario autenticado
     * @param expirationMs tiempo de expiración en milisegundos
     * @return token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationMs) {
        return buildToken(extraClaims, userDetails, expirationMs);
    }

    /**
     * Obtiene el tiempo de expiración configurado para los tokens.
     * @return tiempo de expiración en milisegundos
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Construye un token JWT con los parámetros especificados.
     * @param extraClaims claims adicionales
     * @param userDetails detalles del usuario
     * @param expiration tiempo de expiración
     * @return token JWT construido
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Valida si un token es válido para un usuario.
     * @param token token JWT a validar
     * @param userDetails detalles del usuario
     * @return true si el token es válido y pertenece al usuario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado.
     * @param token token JWT
     * @return true si el token ha expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     * @param token token JWT
     * @return fecha de expiración
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todos los claims del token.
     * @param token token JWT
     * @return objeto Claims con todos los datos del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave de firma a partir de la clave secreta.
     * @return SecretKey para firmar/verificar tokens
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}