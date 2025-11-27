package com.example.springboot_01.controller;

import com.example.springboot_01.dto.ChatRequest;
import com.example.springboot_01.dto.ChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testChatEndpoint() {
        // 1. 准备请求数据
        ChatRequest request = new ChatRequest();
        request.setMessage("你好，这是一个集成测试");

        // 2. 发送 POST 请求到 /chat
        ResponseEntity<ChatResponse> response = restTemplate.postForEntity("/chat", request, ChatResponse.class);

        // 3. 验证响应状态码
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 4. 验证响应内容
        ChatResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getReply()).isNotBlank();

        System.out.println("集成测试响应: " + body.getReply());
    }
}
