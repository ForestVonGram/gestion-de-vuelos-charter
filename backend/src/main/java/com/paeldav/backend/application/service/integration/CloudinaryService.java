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
 * Maneja la carga, transformación automática y eliminación de archivos multimedia en la nube.
 */
@Service
@Slf4j
public class CloudinaryService {

    // Conjuntos inmutables para validar de forma estricta los tipos de archivo permitidos para imágenes
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

    // Inyección de credenciales mediante el constructor para inicializar el cliente de Cloudinary
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        // Configurar la instancia principal de Cloudinary usando el SDK oficial
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    /**
     * Carga una imagen a Cloudinary aplicando validaciones de seguridad.
     * Acepta únicamente: jpg, jpeg, png, avif o webp.
     * Si la imagen original NO es webp, le indica a Cloudinary que la convierta al vuelo (on-the-fly) para optimizar el peso.
     *
     * @param file   archivo de imagen a cargar proveniente de la petición HTTP
     * @param folder carpeta de destino organizativa dentro de Cloudinary (ej: "aeronaves/matricula")
     * @return Map con url segura (HTTPS), public_id único y tamaño del archivo resultante en bytes
     */
    public Map<String, Object> uploadImage(MultipartFile file, String folder) {
        String originalFilename = file != null ? file.getOriginalFilename() : null;
        log.info("Subiendo imagen {} a Cloudinary en carpeta {}", originalFilename, folder);

        // Validación inicial: rechazar peticiones sin archivo físico
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }

        // Extraer metadatos para validar la naturaleza del archivo
        String extension = getExtension(originalFilename);
        String contentType = normalize(file.getContentType());

        boolean allowedByContentType = contentType != null && ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType);
        boolean allowedByExtension = extension != null && ALLOWED_IMAGE_EXTENSIONS.contains(extension);

        // Seguridad: Asegurar que el archivo cumpla con ambas condiciones (MIME type y extensión)
        if (!allowedByContentType && !allowedByExtension) {
            throw new IllegalArgumentException("Formato de imagen no soportado. Formatos permitidos: jpg, jpeg, png, avif, webp");
        }

        boolean isWebp = "image/webp".equals(contentType) || "webp".equals(extension);

        try {
            // Construir un identificador único combinando el nombre base y un timestamp para evitar colisiones
            String baseName = getBaseName(originalFilename);
            String publicId = folder + "/" + baseName + "_" + System.currentTimeMillis();

            // Configurar los parámetros de subida para la API de Cloudinary
            Map<String, Object> options = new HashMap<>();
            options.put("public_id", publicId);
            options.put("resource_type", "image");
            options.put("folder", folder);

            // Regla de optimización: Forzar la conversión a WebP si no viene en ese formato
            if (!isWebp) {
                options.put("format", "webp");
            }

            // Ejecutar la subida del arreglo de bytes a la nube
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            // Extraer y mapear únicamente los datos relevantes de la respuesta de Cloudinary
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("public_id", uploadResult.get("public_id"));
            resultado.put("url", uploadResult.get("secure_url")); // URL HTTPS generada
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
     * Carga un archivo genérico a Cloudinary sin transformaciones de imagen.
     * Útil para documentos PDF, manuales, u otros binarios ("raw").
     *
     * @param file   archivo a cargar
     * @param folder carpeta de destino (ej: "aeronaves/manuales")
     * @return Map con url, public_id y size del archivo
     */
    public Map<String, Object> uploadFile(MultipartFile file, String folder) {
        log.info("Subiendo archivo {} a Cloudinary en carpeta {}", file.getOriginalFilename(), folder);

        try {
            // Generar identificador único
            String baseName = getBaseName(file.getOriginalFilename());
            String publicId = folder + "/" + baseName + "_" + System.currentTimeMillis();

            // Subir usando resource_type="auto" para que Cloudinary detecte si es imagen, video o archivo raw
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto",
                            "folder", folder
                    )
            );

            // Mapear respuesta
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
     * Elimina permanentemente un archivo almacenado en Cloudinary utilizando su identificador único.
     *
     * @param publicId ID público del archivo (ej: "aeronaves/matricula/mi_foto_12345")
     */
    public void deleteFile(String publicId) {
        log.info("Eliminando archivo {} de Cloudinary", publicId);

        try {
            // Enviar petición de destrucción a la API de Cloudinary
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

    // --- Métodos Privados Auxiliares para manipulación segura de nombres y tipos de archivo ---

    /**
     * Extrae el nombre del archivo sin su extensión.
     */
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

    /**
     * Extrae la extensión final del archivo en minúsculas.
     */
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

    /**
     * Limpia la ruta del archivo, previniendo vulnerabilidades de Path Traversal (ej: "../../../archivo.jpg").
     */
    private static String stripPath(String filename) {
        if (filename == null) {
            return null;
        }
        String normalized = filename.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        return slash >= 0 ? normalized.substring(slash + 1) : normalized;
    }

    /**
     * Normaliza el Content-Type (MIME type) a minúsculas y sin espacios.
     */
    private static String normalize(String contentType) {
        if (contentType == null) {
            return null;
        }
        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Convierte de manera segura cualquier tipo de dato numérico proveniente de Cloudinary a un tipo Long de Java.
     */
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