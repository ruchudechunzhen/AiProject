package com.example.aispringaidemo.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.aispringaidemo.advisor.PIIAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ChatClientConfig {

    @Autowired
    private DashScopeChatModel chatModel;

    @Autowired
    @Qualifier("jdbcChatMemory")
    private ChatMemory jdbcChatMemory;

    @Value("classpath:files/test01.st")
    private Resource resource;

    @Value("classpath:files/PII.st")
    private Resource PIIResource;

    @Autowired
    private PIIAdvisor piiAdvisor;

    @Bean("jdbcChatClient")
    public ChatClient getJdbcChatClient(){
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(jdbcChatMemory).build()
                )
                .build();
    }

    @Bean("cleanChatClient")
    public ChatClient getcleanChatClient(){
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(jdbcChatMemory).build()
                )
                .defaultSystem(resource)
                .build();
    }

    @Bean("PIIChatClient")
    public ChatClient getPIIChatClient(){
        // 这里的chatModel可以配置一个本地的小模型，
        // 专门用于数据脱敏，这样也不会把敏感数据通过http发送出去
        return ChatClient.builder(chatModel)
                .defaultSystem(PIIResource)
                .build();
    }

    @Bean("PIIAdvisorChatClient")
    public ChatClient getPIIAdvisorChatClient(){
        return ChatClient.builder(chatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(jdbcChatMemory).build(),piiAdvisor)
                .build();
    }

}
