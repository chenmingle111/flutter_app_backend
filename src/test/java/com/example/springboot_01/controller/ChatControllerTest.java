package com.example.springboot_01.controller;

import com.example.springboot_01.dto.ChatRequest;
import com.example.springboot_01.dto.ChatResponse;
import com.example.springboot_01.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testChat() throws Exception {
        // Mock service response
        String expectedReply = "Hello, I am AI.";
        when(chatService.chat(anyString())).thenReturn(expectedReply);

        // Create request
        ChatRequest request = new ChatRequest();
        request.setMessage("Hello");

        // Perform POST request
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value(expectedReply));
    }

    @Test
    public void testChatEmptyMessage() throws Exception {
        // Create request with empty message
        ChatRequest request = new ChatRequest();
        request.setMessage("");

        // Perform POST request
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()); // Or BadRequest depending on exception handling
    }
}
