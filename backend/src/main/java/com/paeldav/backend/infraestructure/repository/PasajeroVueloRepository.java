package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.PasajeroVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de acceso a datos de la entidad PasajeroVuelo.
 * Maneja la relación y el registro de los pasajeros asignados a los vuelos.
 */
@Repository
public interface PasajeroVueloRepository extends JpaRepository<PasajeroVuelo, Long> {

    // Obtiene la lista completa de pasajeros que están registrados en un vuelo específico
    List<PasajeroVuelo> findByVueloId(Long vueloId);

    // Busca un pasajero exacto dentro de un vuelo utilizando su documento de identidad.
    // Retorna un Optional para manejar de forma segura el caso en el que no se encuentre.
    Optional<PasajeroVuelo> findByVueloIdAndDocumentoIdentidad(Long vueloId, String documentoIdentidad);

    // Verifica de forma rápida si un pasajero ya está registrado en un vuelo.
    // Muy útil para validaciones antes de guardar y evitar asientos duplicados.
    boolean existsByVueloIdAndDocumentoIdentidad(Long vueloId, String documentoIdentidad);

    // Recupera el historial completo de todos los vuelos en los que ha estado un pasajero en particular
    List<PasajeroVuelo> findByDocumentoIdentidad(String documentoIdentidad);

    // Calcula el número total de pasajeros actuales en un vuelo.
    // Ideal para validar la ocupación contra la capacidad máxima de la aeronave.
    long countByVueloId(Long vueloId);
}