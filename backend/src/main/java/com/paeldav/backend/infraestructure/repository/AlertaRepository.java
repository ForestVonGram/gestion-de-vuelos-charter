package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Alerta;
import com.paeldav.backend.domain.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByAeronaveId(Long aeronaveId);

    List<Alerta> findByAeronaveIdAndActiva(Long aeronaveId, Boolean activa);

    List<Alerta> findByTipo(TipoAlerta tipo);

    List<Alerta> findByActiva(Boolean activa);

    @Query("SELECT a FROM Alerta a WHERE a.aeronave.id = :aeronaveId AND a.activa = true ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasActivasPorAeronave(@Param("aeronaveId") Long aeronaveId);

    @Query("SELECT a FROM Alerta a WHERE a.tipo = :tipo AND a.activa = true ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasActivasPorTipo(@Param("tipo") TipoAlerta tipo);

    @Query("SELECT a FROM Alerta a WHERE a.aeronave.id = :aeronaveId AND a.tipo = :tipo AND a.activa = true")
    List<Alerta> findAlertasActivasPorAeronaveYTipo(@Param("aeronaveId") Long aeronaveId, @Param("tipo") TipoAlerta tipo);

    @Query("SELECT a FROM Alerta a WHERE a.mantenimientoRelacionado.id = :mantenimientoId")
    List<Alerta> findByMantenimientoRelacionadoId(@Param("mantenimientoId") Long mantenimientoId);

    @Query("SELECT a FROM Alerta a WHERE a.aeronave.id = :aeronaveId AND a.fechaCreacion BETWEEN :inicio AND :fin ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasPorAeronaveYFecha(@Param("aeronaveId") Long aeronaveId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
