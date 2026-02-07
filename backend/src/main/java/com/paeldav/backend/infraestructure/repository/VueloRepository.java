package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    List<Vuelo> findByEstado(EstadoVuelo estado);

    List<Vuelo> findByUsuarioId(Long usuarioId);

    List<Vuelo> findByAeronaveId(Long aeronaveId);

    @Query("SELECT v FROM Vuelo v WHERE v.fechaSalidaProgramada BETWEEN :inicio AND :fin")
    List<Vuelo> findByFechaSalidaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen OR v.destino = :destino")
    List<Vuelo> findByOrigenOrDestino(@Param("origen") String origen, @Param("destino") String destino);

    @Query("SELECT v FROM Vuelo v JOIN v.tripulacion t WHERE t.id = :tripulanteId")
    List<Vuelo> findByTripulanteId(@Param("tripulanteId") Long tripulanteId);

    List<Vuelo> findByEstadoIn(List<EstadoVuelo> estados);
}
