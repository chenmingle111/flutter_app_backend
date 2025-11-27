package com.example.springboot_01.controller;

import com.example.springboot_01.dto.ChatRequest;
import com.example.springboot_01.dto.ChatResponse;
import com.example.springboot_01.service.ChatService;
import org.springframework.web.bind.annotation.*;

/**
 * ChatController handles AI chat interactions.
 * It provides endpoints for simple chat responses.
 */
@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Generates a chat response from the AI model based on the user's message.
     *
     * @param request The chat request containing the user's message.
     * @return A ChatResponse containing the AI's reply.
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String userMessage = request.getMessage();

        // Validate input
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        String reply = chatService.chat(userMessage);
        return new ChatResponse(reply);
    }
}
