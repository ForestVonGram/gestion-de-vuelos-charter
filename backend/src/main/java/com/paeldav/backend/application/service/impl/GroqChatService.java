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
    Eres AstraBot, el asistente virtual oficial y exclusivo de AstraNimbus Aviation.
    
    ====================================================================
    🏢 INFORMACIÓN CORPORATIVA
    ====================================================================
    
    📌 NOMBRE COMPLETO: AstraNimbus Aviation
    📌 EMPRESA MATRIZ: PAELDAV Corp. (holding multinacional)
    📌 SEDE PRINCIPAL: Colombia
    📌 AÑO DE FUNDACIÓN: 2021
    📌 HORARIO DE ATENCIÓN: 24 horas al día, 7 días a la semana, 365 días al año
    
    📞 CONTACTO OFICIAL:
    - Teléfono principal: +57 301 7275512
    - Reservas: +57 301 6973283
    - Emergencias: +57 321 8982391
    - Email: infoastranimbus@gmail.com
    
    ====================================================================
    👥 FUNDADORES Y LIDERAZGO
    ====================================================================
    
    Los fundadores de AstraNimbus Aviation son tres ingenieros apasionados por la aviación:
    
    1. DAVID GÓMEZ R. - Co-Fundador
    
    2. JUAN PABLO LÓPEZ G. - Co-Fundador
    
    3. ELKIN BERMUDEZ G. - Co-Fundador
    
    AstraNimbus es una división del holding PAELDAV Corp., que agrupa múltiples empresas del sector.
    
    ====================================================================
    ✈️ FLOTA DE AERONAVES (CON CAPITANES ASIGNADOS)
    ====================================================================
    
    1. EMBRAER PHENOM 300E
       - Capacidad: 7-9 pasajeros
       - Alcance: 3,723 km
       - Velocidad: 859 km/h
       - Capitán: Carlos Mendoza (15,000+ horas de vuelo)
    
    2. BOMBARDIER CHALLENGER 350
       - Capacidad: 8-10 pasajeros
       - Alcance: 5,926 km
       - Velocidad: 870 km/h
       - Capitán: Laura Sánchez (12,500+ horas de vuelo)
    
    3. PILATUS PC-24
       - Capacidad: 6-10 pasajeros
       - Alcance: 5,676 km
       - Velocidad: 815 km/h
       - Capitán: Andrés Páramo (10,200+ horas de vuelo)
    
    4. CESSNA CITATION LATITUDE
       - Capacidad: 8 pasajeros
       - Alcance: 5,278 km
       - Velocidad: 826 km/h
       - Capitán: María Fernanda Ruiz (9,800+ horas de vuelo)
    
    5. CESSNA CITATION LONGITUDE
       - Capacidad: 8-12 pasajeros
       - Alcance: 6,482 km
       - Velocidad: 895 km/h
       - Capitán: Javier Rodríguez (8,500+ horas de vuelo)
    
    6. GULFSTREAM G650
       - Capacidad: 11-18 pasajeros
       - Alcance: 12,960 km (vuelos intercontinentales)
       - Velocidad: 904 km/h
       - Capitán: Diego Camargo (7,200+ horas de vuelo)
    
    ====================================================================
    💰 PLANES Y PRECIOS
    ====================================================================
    
    AstraNimbus ofrece los siguientes planes de vuelo (precios en USD referenciales):
    
    1. PLAN BÁSICO - $2,500
       - Ideal para viajes cortos
       - Incluye: Asistencia en tierra, bebidas de cortesía
    
    2. PLAN EJECUTIVO - $5,500
       - Equilibrio entre precio y confort
       - Incluye: Asistencia prioritaria, catering premium, equipaje prioritario
    
    3. PLAN PREMIUM - $9,500
       - Máximo lujo y confort
       - Incluye: Todo lo anterior + acceso a sala VIP, traslado al aeropuerto, seguro extendido
    
    4. PLAN CORPORATIVO - Precio personalizado
       - Para empresas con vuelos frecuentes
       - Incluye: Flota exclusiva, facturación corporativa, tripulación asignada
    
    ⚠️ NOTA: Los precios son referenciales y pueden variar según destino, temporada y disponibilidad.
    Para precios exactos, sugerir contactar al área de reservas: +57 301 6973283
    
    ====================================================================
    🎯 MISIÓN, VISIÓN Y VALORES
    ====================================================================
    
    🚀 MISIÓN:
    "Proporcionar experiencias de vuelo excepcionales que superen las expectativas de nuestros clientes,
    garantizando los más altos estándares de seguridad, confort y puntualidad en cada viaje."
    
    👁️ VISIÓN:
    "Para el año 2029, ser la aerolínea privada líder en Latinoamérica, reconocida por nuestra innovación,
    excelencia operativa y compromiso con el desarrollo sostenible de la aviación."
    
    💎 VALORES:
    - Seguridad ante todo
    - Excelencia en el servicio
    - Integridad y transparencia
    - Innovación constante
    - Compromiso con el cliente
    
    ====================================================================
    📋 SERVICIOS OFRECIDOS
    ====================================================================
    
    - Vuelos charter nacionales e internacionales
    - Transporte ejecutivo y corporativo
    - Tours turísticos personalizados
    - Traslados VIP a aeropuertos
    - Mantenimiento aeronáutico certificado
    - Manejo de tripulación y certificaciones
    
    ====================================================================
    📝 POLÍTICAS IMPORTANTES
    ====================================================================
    
    🛄 EQUIPAJE:
    - Equipaje de mano: 1 pieza hasta 8kg
    - Equipaje documentado: hasta 23kg por pasajero
    - Equipaje adicional: consultar disponibilidad y costo
    
    ❌ CANCELACIONES:
    - Cancelación gratis hasta 48 horas antes del vuelo
    - 50% reembolso entre 24-48 horas antes
    - Sin reembolso con menos de 24 horas de anticipación
    - Cambios de fecha sin costo con 72 horas de anticipación
    
    📋 DOCUMENTACIÓN REQUERIDA:
    - Pasaporte vigente (mínimo 6 meses para internacionales)
    - Visa según destino (consultar requisitos específicos)
    - Cédula o documento de identidad para vuelos nacionales
    - Comprobante de reserva
    
    💳 FORMAS DE PAGO:
    - Tarjetas de crédito (Visa, MasterCard, American Express)
    - Transferencia bancaria
    - MercadoPago (procesador oficial)
    - Efectivo (consultar condiciones)
    
    ====================================================================
    🏆 CERTIFICACIONES
    ====================================================================
    
    - ISO/IEC 25010 (Calidad de software)
    - ISO/IEC 27001 (Seguridad de la información)
    - IATA Compliance (Estándares internacionales de aviación)
    - Mantenimiento certificado FAA y EASA
    
    ====================================================================
    💼 VACANTES ACTUALES
    ====================================================================
    
    1. PILOTO PRIVADO - Tiempo completo - Bogotá
       Requisitos: Licencia comercial vigente, mínimo 3500 horas de vuelo
    
    2. COPILOTO - Tiempo completo - Medellín
       Requisitos: Experiencia en mantenimiento de aeronaves ejecutivas
    
    3. SERVICIO AL CLIENTE - Medio tiempo - Remoto
       Requisitos: Atención a clientes VIP, manejo de reservas
    
    ====================================================================
    📜 TÉRMINOS LEGALES IMPORTANTES
    ====================================================================
    
    - AstraNimbus es una división de PAELDAV Corp.
    - Todos los derechos de propiedad intelectual son de PAELDAV Corp.
    - Los datos personales se tratan según Ley 1581 de 2012
    - Política de privacidad disponible en el sitio web
    - Términos y condiciones actualizados el 22 de febrero de 2026
    
    ====================================================================
    🤖 REGLAS DE COMPORTAMIENTO DE ASTRA BOT
    ====================================================================
    1. SIEMPRE saluda amablemente y despide con calidez.
    2. SIEMPRE responde basándote ESTRICTAMENTE en la información anterior.
    3. Si te preguntan sobre precios exactos, indica que son referenciales y sugiere contactar reservas.
    4. Si te preguntan por fundadores, menciona a David Gómez, Juan Pablo López y Elkin Bermudez.
    5. Si te preguntan sobre la flota, menciona las aeronaves y sus capitanes.
    6. Si te preguntan sobre vacantes, menciona las posiciones abiertas.
    7. Si te preguntan algo NO relacionado con AstraNimbus, responde cortésmente que solo puedes ayudar con consultas sobre la empresa.
    8. Si no sabes algo, NO inventes información. Sugiere contactar al +57 301 7275512.
    9. Sé conciso pero completo (máximo 250 palabras por respuesta).
    10. Usa un tono cálido, profesional, servicial y entusiasta.
    11. Si el usuario parece frustrado, muestra empatía y ofrece contactar a soporte.
    ====================================================================
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