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
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImagenAeronaveServiceImpl implements ImagenAeronaveService {

    private final ImagenAeronaveRepository imagenRepository;
    private final AeronaveRepository aeronaveRepository;
    private final CloudinaryService cloudinaryService;
    private final ImagenAeronaveMapper imagenMapper;

    private static final String CARPETA_IMAGENES = "aeronaves";

    @Override
    public ImagenAeronaveDTO cargarImagen(Long aeronaveId, MultipartFile file, ImagenAeronaveCreateDTO imagenDTO) {
        log.info("Cargando imagen para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave existe
        Aeronave aeronave = aeronaveRepository.findById(aeronaveId)
                .orElseThrow(() -> {
                    log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
                    return new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
                });

        // Validar que el archivo no está vacío
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida");
        }

        try {
            // Crear carpeta específica para la aeronave usando su matrícula
            String carpeta = CARPETA_IMAGENES + "/" + aeronave.getMatricula();

            // Subir a Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file, carpeta);

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

            // Guardar en la base de datos
            ImagenAeronave imagenGuardada = imagenRepository.save(imagen);
            log.info("Imagen cargada exitosamente para aeronave ID: {}, Imagen ID: {}", aeronaveId, imagenGuardada.getId());

            return imagenMapper.toDTO(imagenGuardada);

        } catch (Exception e) {
            log.error("Error al cargar imagen para aeronave ID: {}: {}", aeronaveId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ImagenAeronaveDTO> cargarMultiplesImagenes(Long aeronaveId, List<MultipartFile> files, ImagenAeronaveCreateDTO imagenDTO) {
        log.info("Cargando {} imágenes para aeronave ID: {}", files.size(), aeronaveId);

        return files.stream()
                .map(file -> cargarImagen(aeronaveId, file, imagenDTO))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarImagen(Long aeronaveId, Long imagenId) {
        log.info("Eliminando imagen ID: {} de aeronave ID: {}", imagenId, aeronaveId);

        // Validar que la aeronave existe
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Obtener la imagen
        ImagenAeronave imagen = imagenRepository.findById(imagenId)
                .orElseThrow(() -> {
                    log.warn("Imagen no encontrada con ID: {}", imagenId);
                    return new IllegalArgumentException("Imagen no encontrada con ID: " + imagenId);
                });

        // Validar que la imagen pertenece a la aeronave especificada
        if (!imagen.getAeronave().getId().equals(aeronaveId)) {
            throw new IllegalArgumentException("La imagen no pertenece a la aeronave especificada");
        }

        try {
            // Eliminar de Cloudinary
            cloudinaryService.deleteFile(imagen.getIdCloudinary());

            // Eliminar de la base de datos
            imagenRepository.delete(imagen);
            log.info("Imagen eliminada exitosamente: {}", imagenId);

        } catch (Exception e) {
            log.error("Error al eliminar imagen ID: {}: {}", imagenId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImagenAeronaveDTO> obtenerImagenesAeronave(Long aeronaveId) {
        log.debug("Obteniendo imágenes de aeronave ID: {}", aeronaveId);

        List<ImagenAeronave> imagenes = imagenRepository.findByAeronaveIdOrderByOrdenVisualizacion(aeronaveId);
        return imagenMapper.toDTOList(imagenes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImagenAeronaveDTO> obtenerImagenesPorTipo(Long aeronaveId, TipoImagenAeronave tipo) {
        log.debug("Obteniendo imágenes de tipo {} para aeronave ID: {}", tipo, aeronaveId);

        List<ImagenAeronave> imagenes = imagenRepository.findByAeronaveIdAndTipoOrderByOrdenVisualizacion(aeronaveId, tipo);
        return imagenMapper.toDTOList(imagenes);
    }

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

    @Override
    public void actualizarOrdenImagenes(Long aeronaveId, Map<Long, Integer> ordenUpdates) {
        log.info("Actualizando orden de imágenes para aeronave ID: {}", aeronaveId);

        // Validar que la aeronave existe
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Aeronave no encontrada con ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        ordenUpdates.forEach((imagenId, nuevoOrden) -> {
            ImagenAeronave imagen = imagenRepository.findById(imagenId)
                    .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada con ID: " + imagenId));

            // Validar que la imagen pertenece a la aeronave
            if (!imagen.getAeronave().getId().equals(aeronaveId)) {
                throw new IllegalArgumentException("La imagen no pertenece a la aeronave especificada");
            }

            imagen.setOrdenVisualizacion(nuevoOrden);
            imagenRepository.save(imagen);
        });

        log.info("Orden de imágenes actualizado para aeronave ID: {}", aeronaveId);
    }

    @Override
    public void reordenarImagenesAutomatico(Long aeronaveId) {
        log.info("Reordenando imágenes automáticamente para aeronave ID: {}", aeronaveId);

        List<ImagenAeronave> imagenes = imagenRepository.findByAeronaveIdOrderByOrdenVisualizacion(aeronaveId);

        for (int i = 0; i < imagenes.size(); i++) {
            ImagenAeronave imagen = imagenes.get(i);
            imagen.setOrdenVisualizacion(i);
            imagenRepository.save(imagen);
        }

        log.info("Imágenes reordenadas automáticamente para aeronave ID: {}", aeronaveId);
    }
}
