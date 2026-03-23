package com.paeldav.backend.application.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paeldav.backend.application.service.base.RecaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Implementación del servicio de verificación de reCAPTCHA.
 * Se encarga de validar los tokens de los clientes contra la API de Google
 * para prevenir abusos y accesos automatizados (bots).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecaptchaServiceImpl implements RecaptchaService {

    // Dependencias inyectadas para peticiones HTTP y manejo de JSON
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Valores de configuración inyectados desde el archivo properties/yml
    @Value("${recaptcha.enabled:false}")
    private boolean recaptchaEnabled;

    @Value("${recaptcha.secret-key:}")
    private String recaptchaSecretKey;

    @Value("${recaptcha.score-threshold:0.5}")
    private double scoreThreshold;

    @Value("${recaptcha.verify-url:https://www.google.com/recaptcha/api/siteverify}")
    private String verifyUrl;

    @Override
    public boolean validarToken(String token) {
        // Si el servicio está apagado, se asume como válido por defecto
        if (!estaHabilitado()) {
            log.debug("reCAPTCHA esta deshabilitado");
            return true;
        }

        // Rechazar inmediatamente si el token viene vacío
        if (token == null || token.isBlank()) {
            log.warn("Token de reCAPTCHA vacio o nulo");
            return false;
        }

        // Intentar validar el token con Google y capturar posibles excepciones de red/parseo
        try {
            return validarConGoogle(token);
        } catch (Exception e) {
            log.error("Error validando reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public double obtenerPuntuacion(String token) {
        // Retornar 0.0 si la validación está deshabilitada o el token es inválido
        if (!estaHabilitado() || token == null || token.isBlank()) {
            return 0.0;
        }

        try {
            // Construir el cuerpo de la petición y consultar la API de Google
            String requestBody = "secret=" + recaptchaSecretKey + "&response=" + token;
            String response = restTemplate.postForObject(verifyUrl, requestBody, String.class);

            if (response == null) {
                return 0.0;
            }

            // Extraer y devolver únicamente el puntaje (score) asignado por Google
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("score").asDouble(0.0);
        } catch (IOException e) {
            log.error("Error obteniendo puntuacion de reCAPTCHA: {}", e.getMessage());
            return 0.0;
        }
    }

    @Override
    public boolean estaHabilitado() {
        // Verifica que la bandera esté activa y la clave secreta exista
        return recaptchaEnabled && recaptchaSecretKey != null && !recaptchaSecretKey.isBlank();
    }

    private boolean validarConGoogle(String token) throws IOException {

        // Configurar los encabezados para enviar datos en formato formulario
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Preparar los parámetros requeridos por la API de validación de Google
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", recaptchaSecretKey);
        map.add("response", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // Ejecutar la petición POST hacia el servidor de reCAPTCHA
        String response = restTemplate.postForObject(verifyUrl, request, String.class);

        if (response == null) {
            log.warn("Respuesta vacía de Google reCAPTCHA");
            return false;
        }

        // Parsear la respuesta JSON para su evaluación
        JsonNode jsonNode = objectMapper.readTree(response);

        // Loguear error si Google rechaza la petición (útil para debug)
        if (jsonNode.has("error-codes")) {
            log.error("Google rechazó el token. Errores: {}", jsonNode.get("error-codes"));
        }

        // Verificar el estado general de éxito devuelto
        boolean success = jsonNode.get("success").asBoolean(false);

        if (!success) {
            log.warn("reCAPTCHA validation failed (Success=false)");
            return false;
        }

        // Si es reCAPTCHA v3, validar que el puntaje obtenido supere el umbral mínimo configurado
        if (jsonNode.has("score")) {
            double score = jsonNode.get("score").asDouble(0.0);
            boolean scorePassed = score >= scoreThreshold;
            log.debug("reCAPTCHA score: {} (threshold: {})", score, scoreThreshold);
            return scorePassed;
        }

        // Si no tiene score (v2 legacy), asumimos éxito si success=true
        return true;
    }
}