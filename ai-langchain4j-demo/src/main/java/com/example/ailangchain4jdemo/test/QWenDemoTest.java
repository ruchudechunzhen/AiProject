package com.example.ailangchain4jdemo.test;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class QWenDemoTest {
    public static void main(String[] args) {
        QwenChatModel model = QwenChatModel.builder()
                .apiKey("sk-c1567448faf147e8b2ec11624b1cbe6c")
                .modelName("deepseek-v3.2")
                .build();
        String result = model.chat("hello world");
        System.out.println(result);
    }
}
