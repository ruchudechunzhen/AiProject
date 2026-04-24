package com.example.aispringaidemo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class DashScopeTest {

    @Autowired
    private DashScopeChatModel chatModel;

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

    @Autowired
    private ChatClient chatClient;

    @Test
    public void test01(){
        String result = chatModel.call("你好");
        System.out.println(result);
    }

    @Test
    public void test02(){
        String result = deepSeekChatModel.call("你好你是谁");
        System.out.println(result);
    }

    @Test
    public void test03(){
        PromptTemplate template = PromptTemplate.builder()
                .template("你的名字是{name}")
                .build();
        Map<String, Object> templateMap = new HashMap<>();
        templateMap.put("name","张三");
        String result = chatClient.prompt()
                .system(template.render(templateMap))
                .user("你好")
                .call()
                .content();
        System.out.println(result);
    }

    @Test
    public void test04( @Value("classpath:files/test.st") Resource resource){
        PromptTemplate template = PromptTemplate.builder()
                .resource(resource)
                .build();
        Map<String, Object> templateMap = new HashMap<>();
        templateMap.put("name","李四");
        String result = chatClient.prompt()
                .system(template.render(templateMap))
                .user("你好")
                .call()
                .content();
        System.out.println(result);
    }

    @Test
    public void test05(){
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
}
