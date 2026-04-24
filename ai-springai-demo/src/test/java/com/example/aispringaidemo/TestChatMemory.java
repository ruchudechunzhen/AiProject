package com.example.aispringaidemo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class TestChatMemory {

    // 阿里千问模型
    @Autowired
    private DashScopeChatModel chatModel;

    // 注入redis作为存储介质的chatMemory
    @Autowired
    @Qualifier("redisChatMemory")
    private ChatMemory redisChatMemory;

    @Autowired
    @Qualifier("jdbcChatMemory")
    private ChatMemory jdbcChatMemory;

    @Test
    public void testChatMemory(){
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        String uuid = UUID.randomUUID().toString();
        // 第一轮对话
        chatMemory.add(uuid,new UserMessage("我是一个20岁的java开发"));
        String result01 = chatModel.call(
                Prompt.builder()
                        .messages(chatMemory.get(uuid))
                        .build()
        ).getResult().getOutput().getText();
        System.out.println("第一轮对话结果"+result01);

        chatMemory.add(uuid,new AssistantMessage(result01));
        //第二轮对话
        chatMemory.add(uuid,new UserMessage("我是什么职业"));
        String result02 = chatModel.call(
                Prompt.builder()
                        .messages(chatMemory.get(uuid))
                        .build()
        ).getResult().getOutput().getText();
        System.out.println("第二轮对话结果"+result02);
    }


    @Test
    public void testRedisChatMemory(){
        String uuid = UUID.randomUUID().toString();
        redisChatMemory.add(uuid,new UserMessage("我是一个30岁的厨师"));
        String result01 = chatModel.call(
                Prompt.builder()
                        .messages(redisChatMemory.get(uuid))
                        .build()
        ).getResult().getOutput().getText();
        System.out.println("第一轮对话结果"+result01);
        redisChatMemory.add(uuid,new AssistantMessage(result01));
        redisChatMemory.add(uuid,new UserMessage("我是什么职业"));
        String result02 = chatModel.call(
                Prompt.builder()
                        .messages(redisChatMemory.get(uuid))
                        .build()
        ).getResult().getOutput().getText();
        System.out.println("第二轮对话结果"+result02);

    }

    @Test
    public void testJdbcChatMemory(){
        //String uuid = UUID.randomUUID().toString();
        String uuid = "d0f6395b-38c9-4346-9c9f-aa20127ddbb6";
        jdbcChatMemory.add(uuid,new UserMessage("你好我是一名40岁的教师"));
        String result01 = chatModel.call(
                Prompt.builder()
                        .messages(jdbcChatMemory.get(uuid))
                        .build()
        ).getResult().getOutput().getText();
        System.out.println("第一轮对话结果"+result01);

        jdbcChatMemory.add(uuid,new AssistantMessage(result01));

        // 第二轮对话
        jdbcChatMemory.add(uuid,new UserMessage("我是什么职业"));
        String result02 = chatModel.call(
                Prompt.builder()
                        .messages(jdbcChatMemory.get(uuid))
                        .build()
        ).getResult().getOutput().getText();
        System.out.println("第二轮对话结果"+result02);
    }
}
