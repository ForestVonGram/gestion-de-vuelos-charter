package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AeronaveRepository extends JpaRepository<Aeronave, Long> {

    Optional<Aeronave> findByMatricula(String matricula);

    boolean existsByMatricula(String matricula);

    List<Aeronave> findByEstado(EstadoAeronave estado);

    List<Aeronave> findByModelo(String modelo);

    List<Aeronave> findByCapacidadPasajerosGreaterThanEqual(Integer capacidad);
}
