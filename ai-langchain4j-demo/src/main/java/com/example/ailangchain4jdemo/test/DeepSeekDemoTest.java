package com.example.ailangchain4jdemo.test;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class DeepSeekDemoTest {

    public static void main(String[] args) {
        OpenAiChatModel model =  OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("sk-fbe0f94a60664663bb7f8ef0348b896e")
                .modelName("deepseek-chat")
                .build();
        String result = model.chat("hello");
        System.out.println(result);
    }
}
