package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    Optional<TokenRecuperacion> findByToken(String token);

    @Query("SELECT t FROM TokenRecuperacion t WHERE t.token = :token AND t.usado = false AND t.fechaExpiracion > :now")
    Optional<TokenRecuperacion> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE TokenRecuperacion t SET t.usado = true WHERE t.usuario.id = :usuarioId AND t.usado = false")
    void invalidarTokensAnteriores(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("DELETE FROM TokenRecuperacion t WHERE t.fechaExpiracion < :now")
    void eliminarTokensExpirados(@Param("now") LocalDateTime now);
}
