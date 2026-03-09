package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para la gestión de acceso a datos de la entidad TokenRecuperacion.
 * Controla la generación, validación y limpieza de los enlaces o códigos enviados para restablecer contraseñas.
 */
@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    // Busca un token específico usando la cadena alfanumérica larga (típicamente usada en enlaces por correo)
    Optional<TokenRecuperacion> findByToken(String token);

    // Busca un token usando un código corto (típicamente usado para SMS o interfaces de 6 dígitos)
    Optional<TokenRecuperacion> findByCodigo(String codigo);

    // Consulta optimizada: Trae el token validando que no esté usado ni expirado,
    // y carga simultáneamente la entidad Usuario asociada (JOIN FETCH) para evitar consultas adicionales (N+1).
    @Query("SELECT t FROM TokenRecuperacion t " +
            "JOIN FETCH t.usuario u " +
            "WHERE t.token = :token " +
            "AND t.usado = false " +
            "AND t.fechaExpiracion > :now")
    Optional<TokenRecuperacion> findValidTokenWithUser(
            @Param("token") String token,
            @Param("now") LocalDateTime now);

    // Consulta optimizada: Valida un código corto cruzándolo con el email del usuario para mayor seguridad.
    // También carga al usuario de forma anticipada con JOIN FETCH.
    @Query("SELECT t FROM TokenRecuperacion t " +
            "JOIN FETCH t.usuario u " +
            "WHERE t.codigo = :codigo " +
            "AND u.email = :email " +
            "AND t.usado = false " +
            "AND t.fechaExpiracion > :now")
    Optional<TokenRecuperacion> findValidTokenByCodigoAndEmailWithUser(
            @Param("codigo") String codigo,
            @Param("email") String email,
            @Param("now") LocalDateTime now);

    // Mantenemos los métodos originales por si acaso (Versiones sin JOIN FETCH para consultas más ligeras
    // si no se necesita manipular la entidad Usuario de inmediato)
    @Query("SELECT t FROM TokenRecuperacion t WHERE t.token = :token AND t.usado = false AND t.fechaExpiracion > :now")
    Optional<TokenRecuperacion> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // Validación de código corto cruzado con email (sin JOIN FETCH)
    @Query("SELECT t FROM TokenRecuperacion t WHERE t.codigo = :codigo AND t.usuario.email = :email AND t.usado = false AND t.fechaExpiracion > :now")
    Optional<TokenRecuperacion> findValidTokenByCodigoAndEmail(
            @Param("codigo") String codigo,
            @Param("email") String email,
            @Param("now") LocalDateTime now);

    // Seguridad: Invalida cualquier token previo no usado que tenga el usuario.
    // Se ejecuta al generar un nuevo token para asegurar que solo el último sea válido.
    @Modifying
    @Query("UPDATE TokenRecuperacion t SET t.usado = true WHERE t.usuario.id = :usuarioId AND t.usado = false")
    void invalidarTokensAnteriores(@Param("usuarioId") Long usuarioId);

    // Tarea de mantenimiento: Borra físicamente de la base de datos los tokens que ya superaron su tiempo de vida,
    // evitando que la tabla crezca infinitamente.
    @Modifying
    @Query("DELETE FROM TokenRecuperacion t WHERE t.fechaExpiracion < :now")
    void eliminarTokensExpirados(@Param("now") LocalDateTime now);

    // Consulta adicional útil para limpieza programada o para implementar límites
    // (ej. evitar que un usuario solicite más de X tokens por día)
    @Query("SELECT COUNT(t) FROM TokenRecuperacion t " +
            "WHERE t.usuario.id = :usuarioId AND t.usado = false")
    long countTokensActivosByUsuarioId(@Param("usuarioId") Long usuarioId);
}