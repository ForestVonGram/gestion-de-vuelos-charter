package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Repostaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // Marca esta interfaz como repositorio gestionado por Spring
public interface RepostajeRepository extends JpaRepository<Repostaje, Long> {

    // Obtiene todos los repostajes asociados a una aeronave
    List<Repostaje> findByAeronaveId(Long aeronaveId);

    // Obtiene repostajes asociados a un vuelo específico
    List<Repostaje> findByVueloId(Long vueloId);

    // Obtiene repostajes realizados por un miembro del personal
    List<Repostaje> findByRealizadoPorId(Long personalId);

    // Consulta para obtener repostajes dentro de un rango de fechas
    @Query("SELECT r FROM Repostaje r WHERE r.fechaRepostaje BETWEEN :inicio AND :fin")
    List<Repostaje> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Calcula el total de litros repostados por una aeronave
    @Query("SELECT SUM(r.cantidadLitros) FROM Repostaje r WHERE r.aeronave.id = :aeronaveId")
    Double sumCantidadByAeronaveId(@Param("aeronaveId") Long aeronaveId);

    // Calcula el costo total de repostajes dentro de un rango de fechas
    @Query("SELECT SUM(r.costoTotal) FROM Repostaje r WHERE r.fechaRepostaje BETWEEN :inicio AND :fin")
    Double sumCostoTotalByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Obtiene repostajes filtrados por proveedor de combustible
    List<Repostaje> findByProveedor(String proveedor);

    // Obtiene los últimos repostajes de una aeronave ordenados por fecha descendente
    @Query("SELECT r FROM Repostaje r WHERE r.aeronave.id = :aeronaveId ORDER BY r.fechaRepostaje DESC")
    List<Repostaje> findUltimosRepostajes(@Param("aeronaveId") Long aeronaveId);

    /**
     * Encuentra repostajes en un rango de fechas.
     */
    List<Repostaje> findByFechaRepostajeBetween(LocalDateTime inicio, LocalDateTime fin);
}