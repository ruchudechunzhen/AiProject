package com.example.aispringaidemo.config;

import com.alibaba.cloud.ai.memory.redis.JedisRedisChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Autowired
    private JdbcChatMemoryRepository jdbcChatMemoryRepository;

    @Bean("redisChatMemory")
    public ChatMemory getRedisChatMemory(){
        JedisRedisChatMemoryRepository rediRepository = JedisRedisChatMemoryRepository.builder()
                .host(host)
                .port(port)
                .build();
        // 配置MessageWindowChatMemoryd的存储仓库，将信息存储到redis中。
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(rediRepository)
                .maxMessages(10)
                .build();
    }

    @Bean("jdbcChatMemory")
    public ChatMemory getJdbcChatMemory(){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(5)
                .build();
    }
}
