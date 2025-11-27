package com.example.springboot_01; // 记得改成你的包名

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiConnectionTest {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    void testAiConnection() {
        // 1. 构建 ChatClient
        ChatClient chatClient = chatClientBuilder.build();

        System.out.println("========= 开始发送请求 =========");

        // 2. 发送一条简单的测试消息
        String response = chatClient.prompt()
                .user("你好，这就一个测试，请回复'配置成功'四个字")
                .call()
                .content();

        // 3. 打印结果
        System.out.println("AI 回复内容: " + response);
        System.out.println("========= 测试结束 =========");
    }
}