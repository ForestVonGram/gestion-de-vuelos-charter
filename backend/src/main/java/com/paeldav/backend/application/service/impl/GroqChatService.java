package com.paeldav.backend.application.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paeldav.backend.application.dto.chatbot.ChatbotResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GroqChatService {

    private final OkHttpClient httpClient;
    private final Gson gson;

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // Contexto del sistema
    private static final String SYSTEM_CONTEXT = """
        Eres un asistente virtual llamado "AstraBot" de AstraNimbus Aviation.
        Personalidad: Amable, profesional, servicial y concisa.
        
        Temas que puedes responder:
        - Gestión de vuelos charter
        - Reservas y pasajes aéreos
        - Servicios de AstraNimbus Aviation
        - Documentación de viaje (pasaportes, visas)
        - Políticas de equipaje
        - Información de aeropuertos
        
        Reglas:
        1. Si la pregunta no está relacionada con aviación o viajes, responde:
           "Lo siento, solo puedo ayudarte con consultas sobre vuelos y servicios de AstraNimbus Aviation."
        2. Sé conciso (máximo 150 palabras)
        3. Si no sabes algo, sugiere contactar a soporte al cliente
        4. Usa un tono cálido y profesional
        """;

    public GroqChatService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public ChatbotResponse sendMessage(String userMessage, String userEmail) {
        log.info("Groq request - User: {}, Message: {}", userEmail, userMessage);

        try {
            // Validar entrada
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ChatbotResponse.builder()
                        .reply("Por favor, escribe un mensaje para poder ayudarte.")
                        .timestamp(LocalDateTime.now())
                        .success(false)
                        .errorMessage("Mensaje vacío")
                        .build();
            }

            // Construir el cuerpo de la petición
            String requestBody = buildRequestBody(userMessage);

            // Construir la petición HTTP
            Request request = new Request.Builder()
                    .url(GROQ_API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            // Ejecutar petición
            long startTime = System.currentTimeMillis();
            try (Response response = httpClient.newCall(request).execute()) {
                long responseTime = System.currentTimeMillis() - startTime;

                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    log.error("Groq API error: {} - {}", response.code(), errorBody);
                    return ChatbotResponse.builder()
                            .reply("Lo siento, hubo un error al procesar tu solicitud. Por favor intenta de nuevo.")
                            .timestamp(LocalDateTime.now())
                            .success(false)
                            .errorMessage("API error: " + response.code())
                            .build();
                }

                String responseBody = response.body().string();
                String aiReply = parseResponse(responseBody);

                log.info("Groq response - User: {}, Response time: {}ms", userEmail, responseTime);

                return ChatbotResponse.builder()
                        .reply(aiReply)
                        .timestamp(LocalDateTime.now())
                        .success(true)
                        .build();
            }

        } catch (IOException e) {
            log.error("Network error calling Groq API: {}", e.getMessage(), e);
            return ChatbotResponse.builder()
                    .reply("Lo siento, no puedo conectarme al servicio en este momento. Por favor intenta de nuevo más tarde.")
                    .timestamp(LocalDateTime.now())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ChatbotResponse.builder()
                    .reply("Lo siento, ocurrió un error inesperado. Por favor intenta de nuevo.")
                    .timestamp(LocalDateTime.now())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    private String buildRequestBody(String userMessage) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 500);

        JsonArray messages = new JsonArray();

        // Mensaje del sistema
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", SYSTEM_CONTEXT);
        messages.add(systemMessage);

        // Mensaje del usuario
        JsonObject userMessageObj = new JsonObject();
        userMessageObj.addProperty("role", "user");
        userMessageObj.addProperty("content", userMessage);
        messages.add(userMessageObj);

        requestBody.add("messages", messages);

        return gson.toJson(requestBody);
    }

    private String parseResponse(String responseBody) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices != null && choices.size() > 0) {
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                JsonObject message = firstChoice.getAsJsonObject("message");
                if (message != null && message.has("content")) {
                    return message.get("content").getAsString();
                }
            }
            return "No pude procesar la respuesta. Por favor intenta de nuevo.";
        } catch (Exception e) {
            log.error("Error parsing Groq response: {}", e.getMessage());
            return "Error procesando la respuesta del asistente.";
        }
    }
}