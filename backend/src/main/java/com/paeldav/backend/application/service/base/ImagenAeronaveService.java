package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveDTO;
import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz para la gestión de imágenes de aeronaves.
 * Define operaciones para cargar, eliminar y consultar imágenes.
 */
public interface ImagenAeronaveService {

    /**
     * Carga una imagen para una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param file archivo de imagen a cargar
     * @param imagenDTO datos de la imagen (tipo, descripción, orden)
     * @return DTO con los datos de la imagen cargada
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si la aeronave no existe
     */
    ImagenAeronaveDTO cargarImagen(Long aeronaveId, MultipartFile file, ImagenAeronaveCreateDTO imagenDTO);

    /**
     * Carga múltiples imágenes para una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param files lista de archivos de imagen a cargar
     * @param imagenDTO datos de las imágenes (tipo, descripción, orden)
     * @return lista de DTOs con los datos de las imágenes cargadas
     */
    List<ImagenAeronaveDTO> cargarMultiplesImagenes(Long aeronaveId, List<MultipartFile> files, ImagenAeronaveCreateDTO imagenDTO);

    /**
     * Elimina una imagen de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param imagenId ID de la imagen a eliminar
     * @throws com.paeldav.backend.exception.AeronaveNoEncontradaException si la aeronave no existe
     */
    void eliminarImagen(Long aeronaveId, Long imagenId);

    /**
     * Obtiene todas las imágenes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return lista de DTOs con las imágenes de la aeronave
     */
    List<ImagenAeronaveDTO> obtenerImagenesAeronave(Long aeronaveId);

    /**
     * Obtiene las imágenes de una aeronave filtradas por tipo.
     *
     * @param aeronaveId ID de la aeronave
     * @param tipo tipo de imagen a filtrar
     * @return lista de DTOs con las imágenes del tipo especificado
     */
    List<ImagenAeronaveDTO> obtenerImagenesPorTipo(Long aeronaveId, TipoImagenAeronave tipo);

    /**
     * Obtiene una imagen específica.
     *
     * @param imagenId ID de la imagen
     * @return DTO con los datos de la imagen
     */
    ImagenAeronaveDTO obtenerImagen(Long imagenId);

    /**
     * Actualiza el orden de visualización de las imágenes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param ordenUpdates mapa con imageId -> nuevoOrden
     */
    void actualizarOrdenImagenes(Long aeronaveId, java.util.Map<Long, Integer> ordenUpdates);

    /**
     * Reordena las imágenes automáticamente basado en su fecha de carga.
     *
     * @param aeronaveId ID de la aeronave
     */
    void reordenarImagenesAutomatico(Long aeronaveId);
}
