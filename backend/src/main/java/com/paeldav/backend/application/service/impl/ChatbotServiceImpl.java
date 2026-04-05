package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.chatbot.ChatbotResponse;
import com.paeldav.backend.application.service.base.ChatbotService;
import com.paeldav.backend.application.service.impl.GroqChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private final GroqChatService groqChatService;

    @Override
    @Transactional
    public ChatbotResponse sendMessage(String userMessage, String userEmail) {
        return groqChatService.sendMessage(userMessage, userEmail);
    }
}