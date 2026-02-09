package com.paeldav.backend.application.service.integration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para integración con Cloudinary.
 * Maneja la carga y eliminación de archivos en Cloudinary.
 */
@Service
@Slf4j
public class CloudinaryService {

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
     * Carga un archivo a Cloudinary.
     *
     * @param file archivo a cargar
     * @param folder carpeta de destino (ej: "aeronaves/matricula")
     * @return Map con url, public_id y size del archivo
     * @throws RuntimeException si hay error al cargar
     */
    public Map<String, Object> uploadFile(MultipartFile file, String folder) {
        log.info("Uploadando archivo {} a Cloudinary en carpeta {}", file.getOriginalFilename(), folder);

        try {
            // Generar nombre único para la imagen
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            } else {
                fileName = String.valueOf(System.currentTimeMillis());
            }

            String publicId = folder + "/" + fileName + "_" + System.currentTimeMillis();

            // Subir archivo a Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto",
                            "folder", folder
                    )
            );

            // Preparar respuesta
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("public_id", uploadResult.get("public_id"));
            resultado.put("url", uploadResult.get("secure_url"));
            resultado.put("size", uploadResult.get("bytes"));

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
}
