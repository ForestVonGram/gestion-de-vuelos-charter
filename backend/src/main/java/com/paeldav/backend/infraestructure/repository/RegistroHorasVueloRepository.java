package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.RegistroHorasVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroHorasVueloRepository extends JpaRepository<RegistroHorasVuelo, Long> {

    List<RegistroHorasVuelo> findByTripulanteId(Long tripulanteId);

    List<RegistroHorasVuelo> findByVueloId(Long vueloId);

    List<RegistroHorasVuelo> findByAprobado(Boolean aprobado);

    @Query("SELECT SUM(r.horasVoladas) FROM RegistroHorasVuelo r WHERE r.tripulante.id = :tripulanteId")
    Double sumHorasByTripulanteId(@Param("tripulanteId") Long tripulanteId);

    @Query("SELECT SUM(r.horasVoladas) FROM RegistroHorasVuelo r WHERE r.tripulante.id = :tripulanteId " +
           "AND r.fechaRegistro BETWEEN :inicio AND :fin")
    Double sumHorasByTripulanteIdAndFechaBetween(
            @Param("tripulanteId") Long tripulanteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    List<RegistroHorasVuelo> findByTripulanteIdAndAprobado(Long tripulanteId, Boolean aprobado);
}
