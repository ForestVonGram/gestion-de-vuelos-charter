package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Indica que esta interfaz es un repositorio de Spring Data JPA
public interface TripulanteRepository extends JpaRepository<Tripulante, Long> {



    // Busca un tripulante por su número de licencia
    Optional<Tripulante> findByNumeroLicencia(String numeroLicencia);

    // Verifica si existe un tripulante con el número de licencia indicado
    boolean existsByNumeroLicencia(String numeroLicencia);

    // Obtiene todos los tripulantes según su estado
    List<Tripulante> findByEstado(EstadoTripulante estado);

    // Obtiene tripulantes disponibles que no estén asignados a vuelos programados o en curso
    @Query("SELECT t FROM Tripulante t WHERE t.estado = :estado AND t.id NOT IN " +
            "(SELECT vt.id FROM Vuelo v JOIN v.tripulacion vt WHERE v.estado IN ('PROGRAMADO', 'EN_VUELO'))")
    List<Tripulante> findDisponibles(@Param("estado") EstadoTripulante estado);

    // Busca el tripulante asociado a un usuario específico
    Optional<Tripulante> findByUsuarioId(Long usuarioId);
}