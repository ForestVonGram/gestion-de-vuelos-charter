package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    List<Mantenimiento> findByAeronaveId(Long aeronaveId);

    List<Mantenimiento> findByTipo(TipoMantenimiento tipo);

    List<Mantenimiento> findByAeronaveIdAndTipo(Long aeronaveId, TipoMantenimiento tipo);

    @Query("SELECT m FROM Mantenimiento m WHERE m.fechaMantenimiento BETWEEN :inicio AND :fin")
    List<Mantenimiento> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT m FROM Mantenimiento m WHERE m.aeronave.id = :aeronaveId ORDER BY m.fechaMantenimiento DESC")
    List<Mantenimiento> findUltimosMantenimientos(@Param("aeronaveId") Long aeronaveId);

    List<Mantenimiento> findByRealizadoPorId(Long personalId);
}
