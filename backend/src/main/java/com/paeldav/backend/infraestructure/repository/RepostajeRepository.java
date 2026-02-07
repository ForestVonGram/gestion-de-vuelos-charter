package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Repostaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepostajeRepository extends JpaRepository<Repostaje, Long> {

    List<Repostaje> findByAeronaveId(Long aeronaveId);

    List<Repostaje> findByVueloId(Long vueloId);

    List<Repostaje> findByRealizadoPorId(Long personalId);

    @Query("SELECT r FROM Repostaje r WHERE r.fechaRepostaje BETWEEN :inicio AND :fin")
    List<Repostaje> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(r.cantidadLitros) FROM Repostaje r WHERE r.aeronave.id = :aeronaveId")
    Double sumCantidadByAeronaveId(@Param("aeronaveId") Long aeronaveId);

    @Query("SELECT SUM(r.costoTotal) FROM Repostaje r WHERE r.fechaRepostaje BETWEEN :inicio AND :fin")
    Double sumCostoTotalByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<Repostaje> findByProveedor(String proveedor);

    @Query("SELECT r FROM Repostaje r WHERE r.aeronave.id = :aeronaveId ORDER BY r.fechaRepostaje DESC")
    List<Repostaje> findUltimosRepostajes(@Param("aeronaveId") Long aeronaveId);
}
