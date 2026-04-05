package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.chatbot.ChatRequest;
import com.paeldav.backend.application.dto.chatbot.ChatbotResponse;  // ← Import correcto
import com.paeldav.backend.application.service.base.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
        "https://www.astranimbus.com",
        "https://astranimbus.com",
        "http://localhost:4200",
        "https://gestion-de-vuelos-charter.onrender.com"
})
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatbotResponse> sendMessage(  // ← Cambiado: ChatbotResponse
                                                         @Valid @RequestBody ChatRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails != null ? userDetails.getUsername() : "anonymous";
        log.debug("Chat message from: {}", userEmail);

        ChatbotResponse response = chatbotService.sendMessage(request.getMessage(), userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("{\"status\":\"online\",\"service\":\"AstraBot\"}");
    }
}