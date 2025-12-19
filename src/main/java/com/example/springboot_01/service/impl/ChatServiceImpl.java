package com.example.springboot_01.service.impl;

import com.example.springboot_01.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    private final Resource productDataResource;

    private static final String SYSTEM_PROMPT = """
            你是一个线上商城导购兼客服。
            请优先基于【系统资料】回答用户问题，
            如果资料不足，可以进行合理补充，但不得编造商品信息。

            【系统资料】
            %s
            """;

    public ChatServiceImpl(ChatClient.Builder builder,
            @Value("classpath:product_data.txt") Resource productDataResource) {
        this.productDataResource = productDataResource;
        String productData;
        try {
            productData = productDataResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load product data", e);
        }

        this.chatClient = builder
                .defaultSystem(String.format(SYSTEM_PROMPT, productData))
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
