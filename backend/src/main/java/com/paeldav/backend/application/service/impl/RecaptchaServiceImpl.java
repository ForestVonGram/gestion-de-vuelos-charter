package com.paeldav.backend.application.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paeldav.backend.application.service.base.RecaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecaptchaServiceImpl implements RecaptchaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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
        if (!estaHabilitado()) {
            log.debug("reCAPTCHA esta deshabilitado");
            return true;
        }

        if (token == null || token.isBlank()) {
            log.warn("Token de reCAPTCHA vacio o nulo");
            return false;
        }

        try {
            return validarConGoogle(token);
        } catch (Exception e) {
            log.error("Error validando reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public double obtenerPuntuacion(String token) {
        if (!estaHabilitado() || token == null || token.isBlank()) {
            return 0.0;
        }

        try {
            String requestBody = "secret=" + recaptchaSecretKey + "&response=" + token;
            String response = restTemplate.postForObject(verifyUrl, requestBody, String.class);

            if (response == null) {
                return 0.0;
            }

            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("score").asDouble(0.0);
        } catch (IOException e) {
            log.error("Error obteniendo puntuacion de reCAPTCHA: {}", e.getMessage());
            return 0.0;
        }
    }

    @Override
    public boolean estaHabilitado() {
        return recaptchaEnabled && recaptchaSecretKey != null && !recaptchaSecretKey.isBlank();
    }

    private boolean validarConGoogle(String token) throws IOException {
        String requestBody = "secret=" + recaptchaSecretKey + "&response=" + token;
        String response = restTemplate.postForObject(verifyUrl, requestBody, String.class);

        if (response == null) {
            log.warn("Respuesta vacia de Google reCAPTCHA");
            return false;
        }

        JsonNode jsonNode = objectMapper.readTree(response);
        boolean success = jsonNode.get("success").asBoolean(false);
        
        if (!success) {
            log.warn("reCAPTCHA validation failed");
            return false;
        }

        if (jsonNode.has("score")) {
            double score = jsonNode.get("score").asDouble(0.0);
            boolean scorePassed = score >= scoreThreshold;
            log.debug("reCAPTCHA score: {} (threshold: {})", score, scoreThreshold);
            return scorePassed;
        }

        log.debug("reCAPTCHA v2 validation successful");
        return true;
    }
}
