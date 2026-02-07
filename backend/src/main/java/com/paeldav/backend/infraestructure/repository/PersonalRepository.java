package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {

    Optional<Personal> findByNumeroEmpleado(String numeroEmpleado);

    boolean existsByNumeroEmpleado(String numeroEmpleado);

    List<Personal> findByEstado(EstadoPersonal estado);

    List<Personal> findByCargo(CargoPersonal cargo);

    List<Personal> findByCargoAndEstado(CargoPersonal cargo, EstadoPersonal estado);

    List<Personal> findByAreaEspecializacion(String areaEspecializacion);

    Optional<Personal> findByUsuarioId(Long usuarioId);
}
