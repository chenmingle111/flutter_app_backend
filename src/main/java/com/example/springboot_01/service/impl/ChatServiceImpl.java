package com.example.springboot_01.service.impl;

import com.example.springboot_01.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("你是一个线上商城导购兼客服，帮助顾客解决线上线上购物的问题")
                .build();
    }

    @Override
    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
