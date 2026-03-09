package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.SesionActiva;
import com.paeldav.backend.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de acceso a datos de la entidad SesionActiva.
 * Controla el ciclo de vida de los tokens de autenticación, permitiendo invalidación manual y control de concurrencia.
 */
@Repository
public interface SesionActivaRepository extends JpaRepository<SesionActiva, Long> {

    // Busca un registro de sesión utilizando el hash seguro del token JWT proporcionado
    Optional<SesionActiva> findByTokenHash(String tokenHash);

    // Recupera todas las sesiones que actualmente se encuentran activas para una entidad Usuario específica
    List<SesionActiva> findByUsuarioAndActivaTrue(Usuario usuario);

    // Obtiene la lista de sesiones activas utilizando el identificador (ID) del usuario
    List<SesionActiva> findByUsuarioIdAndActivaTrue(Long usuarioId);

    // Verifica de forma estricta que una sesión exista, esté marcada como activa y su fecha límite no haya sido superada
    @Query("SELECT s FROM SesionActiva s WHERE s.tokenHash = :tokenHash AND s.activa = true AND s.fechaExpiracion > :now")
    Optional<SesionActiva> findValidSession(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);

    // Operación masiva: Cierra (invalida) forzosamente todas las sesiones abiertas de un usuario (ej. al cambiar la contraseña)
    @Modifying
    @Query("UPDATE SesionActiva s SET s.activa = false WHERE s.usuario.id = :usuarioId")
    void revocarTodasLasSesiones(@Param("usuarioId") Long usuarioId);

    // Tarea de limpieza (Cleanup): Marca como inactivas todas las sesiones en la base de datos que ya han caducado cronológicamente
    @Modifying
    @Query("UPDATE SesionActiva s SET s.activa = false WHERE s.fechaExpiracion < :now")
    void revocarSesionesExpiradas(@Param("now") LocalDateTime now);

    // Refresca la marca de tiempo de la última interacción del usuario con el sistema (útil para desconexión por inactividad)
    @Modifying
    @Query("UPDATE SesionActiva s SET s.ultimaActividad = :now WHERE s.tokenHash = :tokenHash")
    void actualizarUltimaActividad(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);

    // Cuenta cuántos dispositivos o navegadores tiene el usuario conectados simultáneamente
    long countByUsuarioIdAndActivaTrue(Long usuarioId);
}