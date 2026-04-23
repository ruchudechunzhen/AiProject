package com.example.aispringaidemo.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class chatConfig {
    private final DashScopeChatModel dashScopeChatModel;

    @Bean
    public ChatClient getChatClient(){
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是小白") //静态配置
                .build();
    }



}
