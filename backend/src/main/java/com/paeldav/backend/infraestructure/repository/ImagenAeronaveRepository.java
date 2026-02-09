package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.ImagenAeronave;
import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de imágenes de aeronaves.
 */
@Repository
public interface ImagenAeronaveRepository extends JpaRepository<ImagenAeronave, Long> {

    /**
     * Obtiene todas las imágenes de una aeronave.
     */
    List<ImagenAeronave> findByAeronaveIdOrderByOrdenVisualizacion(Long aeronaveId);

    /**
     * Obtiene todas las imágenes de una aeronave de un tipo específico.
     */
    List<ImagenAeronave> findByAeronaveIdAndTipoOrderByOrdenVisualizacion(Long aeronaveId, TipoImagenAeronave tipo);

    /**
     * Obtiene una imagen por su ID de Cloudinary.
     */
    Optional<ImagenAeronave> findByIdCloudinary(String idCloudinary);

    /**
     * Verifica si existe una imagen con el ID de Cloudinary especificado.
     */
    boolean existsByIdCloudinary(String idCloudinary);

    /**
     * Elimina una imagen por su ID de Cloudinary.
     */
    void deleteByIdCloudinary(String idCloudinary);

    /**
     * Cuenta las imágenes de una aeronave.
     */
    long countByAeronaveId(Long aeronaveId);
}
