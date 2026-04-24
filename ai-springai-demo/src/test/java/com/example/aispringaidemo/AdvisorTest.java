package com.example.aispringaidemo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.Consumer;

@SpringBootTest
public class AdvisorTest {

    @Autowired
    @Qualifier("jdbcChatClient")
    private ChatClient jdbcChatClient;

    @Autowired
    @Qualifier("cleanChatClient")
    private ChatClient cleanChatClient;


    @Autowired
    @Qualifier("PIIAdvisorChatClient")
    private ChatClient piiAdvisorChatClient;

    @Autowired
    @Qualifier("jdbcChatMemory")
    private ChatMemory jdbcChatMemory;

    private String key = "key002";



    @Test
    public void testJdbcChatClient(){
        String result01 = jdbcChatClient.prompt()
                // 配置记忆存储的key，业务场景下这个key可以来自数据库查询结果，userId+会话id
                .advisors(new Consumer<ChatClient.AdvisorSpec>() {
                    @Override
                    public void accept(ChatClient.AdvisorSpec advisorSpec) {
                        advisorSpec.param(ChatMemory.CONVERSATION_ID,key);
                    }
                })
                .user("我是一个50岁的工人")
                .call()
                .content();
        System.out.println(result01);
        String result02 = jdbcChatClient.prompt()
                .advisors(advisorSpec ->advisorSpec.param(ChatMemory.CONVERSATION_ID,key))
                .user("我的工作是什么")
                .call()
                .content();
        System.out.println(result02);
    }

    @Test
    public void testJdbcChatClient02(){
        List<Message> messages = jdbcChatMemory.get(key);
        if (messages.size()>=3){
            //将历史信息进行摘要
            //本质就是按照配置的模板在访问一次大模型，之后将之前的记忆删除，然后将最新结果存储到数据库中
            AssistantMessage assistantMessage = cleanChatClient.prompt()
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, key))
                    .call()
                    .chatResponse().getResult().getOutput();
            jdbcChatMemory.clear(key);
            jdbcChatMemory.add(key,assistantMessage);
        }

        String result = jdbcChatClient.prompt()
                .advisors(advisorSpec ->advisorSpec.param(ChatMemory.CONVERSATION_ID,key))
                .user("请介绍一下我")
                .call()
                .content();
        System.out.println(result);
    }

    @Test
    public void testPIIChatClient(){
        String content = piiAdvisorChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, key))
                .user("我的身份证号码是 83749279383729475 ，请问我的生日是多少")
                .call()
                .content();
        System.out.println(content);
    }
}
