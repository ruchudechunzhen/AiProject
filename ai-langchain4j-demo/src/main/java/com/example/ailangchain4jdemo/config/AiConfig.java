package com.example.ailangchain4jdemo.config;

import com.example.ailangchain4jdemo.service.ToolService;
import com.example.ailangchain4jdemo.utils.HashMapChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.*;
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

        // 为大模型设计角色，适合金融，医疗，教育等细分领域，回答有深度，同时避免乱回答，规避风险
        @SystemMessage("你是一个名叫小家的Java辅助助手，拥有丰富的Java知识，用户向你问好是礼貌的介绍自己，并对用户的提问做出专业的回答" +
                "日期：{{current_date}}") // 通过占位符配置信息
        TokenStream stream(@MemoryId int memoryId,@UserMessage String message,@V("current_date") String date);
    }

    private final ToolService toolService;

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

    // 使用chatMemory 和 memoryId 做记忆隔离
    @Bean
    public AssistantUnique assistantUnique(ChatModel qwenChatModel,StreamingChatModel qwenStreamingChatModel){
        AssistantUnique assistantUnique = AiServices.builder(AssistantUnique.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10).id(memoryId).build())
                .build();
        return assistantUnique;
    }

    // 使用hashMap实现记忆隔离
    @Bean
    public AssistantUnique assistantUniqueHash(ChatModel qwenChatModel,StreamingChatModel qwenStreamingChatModel){
        AssistantUnique assistantUnique = AiServices.builder(AssistantUnique.class)
                .chatModel(qwenChatModel)
                .tools(toolService) //  添加tool服务，增加大模型与显示世界的联系，实际开发中，可链接数据库添加真实数据
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(memoryId -> new HashMapChatMemory(Integer.getInteger(memoryId.toString()), "10"))
                .build();
        return assistantUnique;
    }
}
