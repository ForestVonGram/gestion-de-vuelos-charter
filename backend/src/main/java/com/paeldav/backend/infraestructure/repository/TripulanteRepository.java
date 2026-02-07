package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripulanteRepository extends JpaRepository<Tripulante, Long> {

    Optional<Tripulante> findByNumeroLicencia(String numeroLicencia);

    boolean existsByNumeroLicencia(String numeroLicencia);

    List<Tripulante> findByEstado(EstadoTripulante estado);

    @Query("SELECT t FROM Tripulante t WHERE t.estado = :estado AND t.id NOT IN " +
           "(SELECT vt.id FROM Vuelo v JOIN v.tripulacion vt WHERE v.estado IN ('PROGRAMADO', 'EN_VUELO'))")
    List<Tripulante> findDisponibles(@Param("estado") EstadoTripulante estado);

    Optional<Tripulante> findByUsuarioId(Long usuarioId);
}
