package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Aeronave.
 * Proporciona métodos para acceder y manipular datos de aeronaves en la base de datos.
 */
@Repository
public interface AeronaveRepository extends JpaRepository<Aeronave, Long> {

    /**
     * Busca una aeronave por su matrícula.
     * @param matricula matrícula de la aeronave
     * @return Optional con la aeronave encontrada o vacío si no existe
     */
    Optional<Aeronave> findByMatricula(String matricula);

    /**
     * Verifica si existe una aeronave con la matrícula especificada.
     * @param matricula matrícula a verificar
     * @return true si ya existe una aeronave con esa matrícula
     */
    boolean existsByMatricula(String matricula);

    /**
     * Obtiene todas las aeronaves con un estado específico.
     * @param estado estado de la aeronave (DISPONIBLE, EN_VUELO, etc.)
     * @return lista de aeronaves con ese estado
     */
    List<Aeronave> findByEstado(EstadoAeronave estado);

    /**
     * Obtiene todas las aeronaves de un modelo específico.
     * @param modelo modelo de la aeronave
     * @return lista de aeronaves con ese modelo
     */
    List<Aeronave> findByModelo(String modelo);

    /**
     * Obtiene todas las aeronaves con capacidad de pasajeros mayor o igual a la especificada.
     * @param capacidad capacidad mínima requerida
     * @return lista de aeronaves que cumplen la capacidad
     */
    List<Aeronave> findByCapacidadPasajerosGreaterThanEqual(Integer capacidad);

    /**
     * Cuenta aeronaves por estado.
     * @param estado estado de la aeronave
     * @return número de aeronaves en ese estado
     */
    long countByEstado(EstadoAeronave estado);
}