package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.VerificacionDosFactores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VerificacionDosFactoresRepository extends JpaRepository<VerificacionDosFactores, Long> {

    /**
     * Encuentra un código válido no verificado y no expirado.
     * Valida que el código no haya sido usado y que aún esté dentro del tiempo de expiración.
     */
    @Query("SELECT v FROM VerificacionDosFactores v WHERE v.codigo = :codigo " +
           "AND v.verificado = false AND v.activo = true " +
           "AND v.fechaExpiracion > CURRENT_TIMESTAMP")
    Optional<VerificacionDosFactores> findValidCode(@Param("codigo") String codigo);

    /**
     * Obtiene la verificación más reciente pendiente de un usuario.
     */
    @Query(value = "SELECT * FROM verificaciones_dos_factores v " +
           "WHERE v.usuario_id = :usuarioId AND v.verificado = false AND v.activo = true " +
           "ORDER BY v.fecha_creacion DESC LIMIT 1",
           nativeQuery = true)
    Optional<VerificacionDosFactores> findLatestByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Invalida todas las verificaciones pendientes (no verificadas) de un usuario.
     * Se utiliza antes de generar un nuevo código.
     */
    @Modifying
    @Transactional
    @Query("UPDATE VerificacionDosFactores v SET v.activo = false " +
           "WHERE v.usuario.id = :usuarioId AND v.verificado = false")
    void invalidateUnverifiedByUsuarioId(@Param("usuarioId") Long usuarioId);
}
