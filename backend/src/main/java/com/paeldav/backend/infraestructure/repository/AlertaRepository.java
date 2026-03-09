package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Alerta;
import com.paeldav.backend.domain.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // Indica que esta interfaz es un repositorio de Spring
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Obtiene todas las alertas asociadas a una aeronave por su ID
    List<Alerta> findByAeronaveId(Long aeronaveId);

    // Obtiene alertas de una aeronave filtrando por estado activa/inactiva
    List<Alerta> findByAeronaveIdAndActiva(Long aeronaveId, Boolean activa);

    // Obtiene alertas según el tipo de alerta
    List<Alerta> findByTipo(TipoAlerta tipo);

    // Obtiene todas las alertas filtrando por si están activas o no
    List<Alerta> findByActiva(Boolean activa);

    // Consulta personalizada para obtener alertas activas de una aeronave ordenadas por fecha de creación
    @Query("SELECT a FROM Alerta a WHERE a.aeronave.id = :aeronaveId AND a.activa = true ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasActivasPorAeronave(@Param("aeronaveId") Long aeronaveId);

    // Consulta personalizada para obtener alertas activas por tipo ordenadas por fecha de creación
    @Query("SELECT a FROM Alerta a WHERE a.tipo = :tipo AND a.activa = true ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasActivasPorTipo(@Param("tipo") TipoAlerta tipo);

    // Consulta para obtener alertas activas filtradas por aeronave y tipo
    @Query("SELECT a FROM Alerta a WHERE a.aeronave.id = :aeronaveId AND a.tipo = :tipo AND a.activa = true")
    List<Alerta> findAlertasActivasPorAeronaveYTipo(@Param("aeronaveId") Long aeronaveId, @Param("tipo") TipoAlerta tipo);

    // Obtiene alertas relacionadas con un mantenimiento específico
    @Query("SELECT a FROM Alerta a WHERE a.mantenimientoRelacionado.id = :mantenimientoId")
    List<Alerta> findByMantenimientoRelacionadoId(@Param("mantenimientoId") Long mantenimientoId);

    // Obtiene alertas de una aeronave dentro de un rango de fechas
    @Query("SELECT a FROM Alerta a WHERE a.aeronave.id = :aeronaveId AND a.fechaCreacion BETWEEN :inicio AND :fin ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasPorAeronaveYFecha(@Param("aeronaveId") Long aeronaveId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}