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

@Repository
public interface SesionActivaRepository extends JpaRepository<SesionActiva, Long> {

    Optional<SesionActiva> findByTokenHash(String tokenHash);

    List<SesionActiva> findByUsuarioAndActivaTrue(Usuario usuario);

    List<SesionActiva> findByUsuarioIdAndActivaTrue(Long usuarioId);

    @Query("SELECT s FROM SesionActiva s WHERE s.tokenHash = :tokenHash AND s.activa = true AND s.fechaExpiracion > :now")
    Optional<SesionActiva> findValidSession(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE SesionActiva s SET s.activa = false WHERE s.usuario.id = :usuarioId")
    void revocarTodasLasSesiones(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE SesionActiva s SET s.activa = false WHERE s.fechaExpiracion < :now")
    void revocarSesionesExpiradas(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE SesionActiva s SET s.ultimaActividad = :now WHERE s.tokenHash = :tokenHash")
    void actualizarUltimaActividad(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);

    long countByUsuarioIdAndActivaTrue(Long usuarioId);
}
