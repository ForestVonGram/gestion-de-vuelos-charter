package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveDTO;
import com.paeldav.backend.application.mapper.ImagenAeronaveMapper;
import com.paeldav.backend.application.service.base.ImagenAeronaveService;
import com.paeldav.backend.application.service.integration.CloudinaryService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.ImagenAeronave;
import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.ImagenAeronaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de imágenes de aeronaves.
 * Maneja carga, eliminación, consulta y ordenamiento de imágenes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImagenAeronaveServiceImpl implements ImagenAeronaveService {

    // Repositorio para persistencia de imágenes
    private final ImagenAeronaveRepository imagenRepository;

    // Repositorio para consultar aeronaves
    private final AeronaveRepository aeronaveRepository;

    // Servicio para subir y eliminar archivos en Cloudinary
    private final CloudinaryService cloudinaryService;

    // Mapper para convertir entidades a DTO
    private final ImagenAeronaveMapper imagenMapper;

    // Carpeta base donde se almacenarán las imágenes en Cloudinary
    private static final String CARPETA_IMAGENES = "aeronaves";

    /**
     * Carga una imagen para una aeronave específica.
     */
    @Override
    public ImagenAeronaveDTO cargarImagen(Long aeronaveId, MultipartFile file, ImagenAeronaveCreateDTO imagenDTO) {
        log.info("Cargando imagen para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave existe
        Aeronave aeronave = aeronaveRepository.findById(aeronaveId)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
                });

        // Validar que el archivo no esté vacío
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }

        try {

            // Crear carpeta en Cloudinary usando la matrícula de la aeronave
            String carpeta = CARPETA_IMAGENES + "/" + aeronave.getMatricula();

            // Subir imagen a Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, carpeta);

            // Crear entidad ImagenAeronave
            ImagenAeronave imagen = ImagenAeronave.builder()
                    .aeronave(aeronave)
                    .urlImagen((String) uploadResult.get("url"))
                    .idCloudinary((String) uploadResult.get("public_id"))
                    .tipo(imagenDTO.getTipo())
                    .descripcion(imagenDTO.getDescripcion())
                    .ordenVisualizacion(imagenDTO.getOrdenVisualizacion() != null ? imagenDTO.getOrdenVisualizacion() : 0)
                    .tamanoBytes((Long) uploadResult.get("size"))
                    .build();

            // Guardar imagen en la base de datos
            ImagenAeronave imagenGuardada = imagenRepository.save(imagen);

            log.info("Imagen cargada exitosamente para aeronave ID: {}, Imagen ID: {}", aeronaveId, imagenGuardada.getId());

            return imagenMapper.toDTO(imagenGuardada);

        } catch (Exception e) {
            log.error("Error al cargar imagen para aeronave ID: {}: {}", aeronaveId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Carga múltiples imágenes para una aeronave.
     */
    @Override
    public List<ImagenAeronaveDTO> cargarMultiplesImagenes(Long aeronaveId, List<MultipartFile> files, ImagenAeronaveCreateDTO imagenDTO) {

        log.info("Cargando {} imágenes para aeronave ID: {}", files.size(), aeronaveId);

        // Procesar cada archivo usando el método de carga individual
        return files.stream()
                .map(file -> cargarImagen(aeronaveId, file, imagenDTO))
                .collect(Collectors.toList());
    }

    /**
     * Elimina una imagen de una aeronave.
     */
    @Override
    public void eliminarImagen(Long aeronaveId, Long imagenId) {

        log.info("Eliminando imagen ID: {} de aeronave ID: {}", imagenId, aeronaveId);

        // Validar que la aeronave existe
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Buscar la imagen en la base de datos
        ImagenAeronave imagen = imagenRepository.findById(imagenId)
                .orElseThrow(() -> {
                    log.warn("Imagen no encontrada con ID: {}", imagenId);
                    return new IllegalArgumentException("Imagen no encontrada con ID: " + imagenId);
                });

        // Validar que la imagen pertenece a la aeronave indicada
        if (!imagen.getAeronave().getId().equals(aeronaveId)) {
            throw new IllegalArgumentException("La imagen no pertenece a la aeronave especificada");
        }

        try {

            // Eliminar archivo en Cloudinary
            cloudinaryService.deleteFile(imagen.getIdCloudinary());

            // Eliminar registro en la base de datos
            imagenRepository.delete(imagen);

            log.info("Imagen eliminada exitosamente: {}", imagenId);

        } catch (Exception e) {
            log.error("Error al eliminar imagen ID: {}: {}", imagenId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Obtiene todas las imágenes de una aeronave ordenadas por visualización.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ImagenAeronaveDTO> obtenerImagenesAeronave(Long aeronaveId) {

        log.debug("Obteniendo imágenes de aeronave ID: {}", aeronaveId);

        List<ImagenAeronave> imagenes = imagenRepository.findByAeronaveIdOrderByOrdenVisualizacion(aeronaveId);

        return imagenMapper.toDTOList(imagenes);
    }

    /**
     * Obtiene imágenes de una aeronave filtradas por tipo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ImagenAeronaveDTO> obtenerImagenesPorTipo(Long aeronaveId, TipoImagenAeronave tipo) {

        log.debug("Obteniendo imágenes de tipo {} para aeronave ID: {}", tipo, aeronaveId);

        List<ImagenAeronave> imagenes = imagenRepository.findByAeronaveIdAndTipoOrderByOrdenVisualizacion(aeronaveId, tipo);

        return imagenMapper.toDTOList(imagenes);
    }

    /**
     * Obtiene una imagen específica por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public ImagenAeronaveDTO obtenerImagen(Long imagenId) {

        log.debug("Obteniendo imagen ID: {}", imagenId);

        ImagenAeronave imagen = imagenRepository.findById(imagenId)
                .orElseThrow(() -> {
                    log.warn("Imagen no encontrada con ID: {}", imagenId);
                    return new IllegalArgumentException("Imagen no encontrada con ID: " + imagenId);
                });

        return imagenMapper.toDTO(imagen);
    }

    /**
     * Actualiza el orden de visualización de las imágenes.
     */
    @Override
    public void actualizarOrdenImagenes(Long aeronaveId, Map<Long, Integer> ordenUpdates) {

        log.info("Actualizando orden de imágenes para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Actualizar el orden de cada imagen
        ordenUpdates.forEach((imagenId, nuevoOrden) -> {

            ImagenAeronave imagen = imagenRepository.findById(imagenId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada con ID: " + imagenId));

            // Verificar que la imagen pertenece a la aeronave
            if (!imagen.getAeronave().getId().equals(aeronaveId)) {
                throw new IllegalArgumentException("La imagen no pertenece a la aeronave especificada");
            }

            imagen.setOrdenVisualizacion(nuevoOrden);

            imagenRepository.save(imagen);
        });

        log.info("Orden de imágenes actualizado para aeronave ID: {}", aeronaveId);
    }

    /**
     * Reordena automáticamente las imágenes de una aeronave.
     */
    @Override
    public void reordenarImagenesAutomatico(Long aeronaveId) {

        log.info("Reordenando imágenes automáticamente para aeronave ID: {}", aeronaveId);

        // Obtener imágenes ordenadas
        List<ImagenAeronave> imagenes = imagenRepository.findByAeronaveIdOrderByOrdenVisualizacion(aeronaveId);

        // Asignar nuevo orden secuencial
        for (int i = 0; i < imagenes.size(); i++) {

            ImagenAeronave imagen = imagenes.get(i);

            imagen.setOrdenVisualizacion(i);

            imagenRepository.save(imagen);
        }

        log.info("Imágenes reordenadas automáticamente para aeronave ID: {}", aeronaveId);
    }
}