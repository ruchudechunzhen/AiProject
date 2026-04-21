package com.example.ailangchain4jdemo.test;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class DeepSeekDemoTest {

    public static void main(String[] args) {
        OpenAiChatModel model =  OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("sk-6b3605a31315417593e2778f813c9d07")
                .modelName("deepseek-chat")
                .build();
        String result = model.chat("hello");
        System.out.println(result);
    }
}
