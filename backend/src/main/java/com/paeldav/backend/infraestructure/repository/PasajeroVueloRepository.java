package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.PasajeroVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasajeroVueloRepository extends JpaRepository<PasajeroVuelo, Long> {

    List<PasajeroVuelo> findByVueloId(Long vueloId);

    Optional<PasajeroVuelo> findByVueloIdAndDocumentoIdentidad(Long vueloId, String documentoIdentidad);

    boolean existsByVueloIdAndDocumentoIdentidad(Long vueloId, String documentoIdentidad);

    List<PasajeroVuelo> findByDocumentoIdentidad(String documentoIdentidad);

    long countByVueloId(Long vueloId);
}
