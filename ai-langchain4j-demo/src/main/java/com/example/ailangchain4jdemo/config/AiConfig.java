package com.example.ailangchain4jdemo.config;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

    // 通过MessageWindowChatMemory 在内存中实现记忆功能
    public interface Assistant{
        String chat (String message);
        TokenStream stream (String message);
    }

    // 通过id实现记忆隔离
    public interface AssistantUnique{
        String chat(@MemoryId int memoryId, @UserMessage String message);
        String stream(@MemoryId int memoryId,@UserMessage String message);
    }

    // 通过MessageWindowChatMemory 在内存中实现记忆功能
    @Bean
    public Assistant assistant(ChatModel qwenChatModel, StreamingChatModel qwenStreamingChatModel){
        // 保留最近 10 轮对话
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 生成Assistant 接口的实现类
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .build();
        return  assistant;
    }

    @Bean
    public AssistantUnique assistantUnique(ChatModel qwenChatModel,StreamingChatModel qwenStreamingChatModel){
        AssistantUnique assistantUnique = AiServices.builder(AssistantUnique.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10).id(memoryId).build())
                .build();
        return assistantUnique;
    }
}
