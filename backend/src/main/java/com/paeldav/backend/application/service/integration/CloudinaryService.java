package com.paeldav.backend.application.service.integration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Servicio para integración con Cloudinary.
 * Maneja la carga y eliminación de archivos en Cloudinary.
 */
@Service
@Slf4j
public class CloudinaryService {

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/avif",
            "image/webp"
    );

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "avif",
            "webp"
    );

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        // Configurar Cloudinary con las credenciales
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    /**
     * Carga una imagen a Cloudinary.
     * Acepta únicamente: jpg, jpeg, png, avif o webp.
     * Si la imagen NO es webp, la convierte a webp automáticamente.
     *
     * @param file   archivo de imagen a cargar
     * @param folder carpeta de destino (ej: "aeronaves/matricula")
     * @return Map con url, public_id y size del archivo
     */
    public Map<String, Object> uploadImage(MultipartFile file, String folder) {
        String originalFilename = file != null ? file.getOriginalFilename() : null;
        log.info("Subiendo imagen {} a Cloudinary en carpeta {}", originalFilename, folder);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }

        String extension = getExtension(originalFilename);
        String contentType = normalize(file.getContentType());

        boolean allowedByContentType = contentType != null && ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType);
        boolean allowedByExtension = extension != null && ALLOWED_IMAGE_EXTENSIONS.contains(extension);

        if (!allowedByContentType && !allowedByExtension) {
            throw new IllegalArgumentException("Formato de imagen no soportado. Formatos permitidos: jpg, jpeg, png, avif, webp");
        }

        boolean isWebp = "image/webp".equals(contentType) || "webp".equals(extension);

        try {
            String baseName = getBaseName(originalFilename);
            String publicId = folder + "/" + baseName + "_" + System.currentTimeMillis();

            Map<String, Object> options = new HashMap<>();
            options.put("public_id", publicId);
            options.put("resource_type", "image");
            options.put("folder", folder);

            if (!isWebp) {
                options.put("format", "webp");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("public_id", uploadResult.get("public_id"));
            resultado.put("url", uploadResult.get("secure_url"));
            resultado.put("size", asLong(uploadResult.get("bytes")));

            log.info("Imagen cargada exitosamente: {}", uploadResult.get("public_id"));
            return resultado;

        } catch (IOException e) {
            log.error("Error de I/O al cargar imagen a Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al cargar imagen: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al cargar imagen a Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al cargar imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Carga un archivo a Cloudinary.
     *
     * @param file   archivo a cargar
     * @param folder carpeta de destino (ej: "aeronaves/matricula")
     * @return Map con url, public_id y size del archivo
     * @throws RuntimeException si hay error al cargar
     */
    public Map<String, Object> uploadFile(MultipartFile file, String folder) {
        log.info("Subiendo archivo {} a Cloudinary en carpeta {}", file.getOriginalFilename(), folder);

        try {
            String baseName = getBaseName(file.getOriginalFilename());
            String publicId = folder + "/" + baseName + "_" + System.currentTimeMillis();

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto",
                            "folder", folder
                    )
            );

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("public_id", uploadResult.get("public_id"));
            resultado.put("url", uploadResult.get("secure_url"));
            resultado.put("size", asLong(uploadResult.get("bytes")));

            log.info("Archivo cargado exitosamente: {}", uploadResult.get("public_id"));
            return resultado;

        } catch (IOException e) {
            log.error("Error de I/O al cargar archivo a Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al cargar archivo: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al cargar archivo a Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al cargar archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un archivo de Cloudinary.
     *
     * @param publicId ID público del archivo en Cloudinary
     * @throws RuntimeException si hay error al eliminar
     */
    public void deleteFile(String publicId) {
        log.info("Eliminando archivo {} de Cloudinary", publicId);

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Archivo eliminado exitosamente: {}", publicId);

        } catch (IOException e) {
            log.error("Error de I/O al eliminar archivo de Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar archivo: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al eliminar archivo de Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar archivo: " + e.getMessage(), e);
        }
    }

    private static String getBaseName(String originalFilename) {
        String filename = stripPath(originalFilename);
        if (filename == null || filename.isBlank()) {
            return String.valueOf(System.currentTimeMillis());
        }
        int dot = filename.lastIndexOf('.');
        if (dot <= 0) {
            return filename;
        }
        return filename.substring(0, dot);
    }

    private static String getExtension(String originalFilename) {
        String filename = stripPath(originalFilename);
        if (filename == null || filename.isBlank()) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String stripPath(String filename) {
        if (filename == null) {
            return null;
        }
        String normalized = filename.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        return slash >= 0 ? normalized.substring(slash + 1) : normalized;
    }

    private static String normalize(String contentType) {
        if (contentType == null) {
            return null;
        }
        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Integer i) {
            return i.longValue();
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception ignored) {
            return null;
        }
    }
}
