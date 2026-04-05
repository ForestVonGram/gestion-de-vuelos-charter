package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.chatbot.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse sendMessage(String userMessage, String userEmail);
}