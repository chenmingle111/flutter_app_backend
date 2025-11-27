package com.example.springboot_01.service;

public interface ChatService {
    /**
     * Generates a chat response based on the user's message.
     *
     * @param message The user's input message.
     * @return The AI's response as a String.
     */
    String chat(String message);
}
