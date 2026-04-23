package com.example.aispringaidemo;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DashScopeTest {

    @Autowired
    private DashScopeChatModel chatModel;

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

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
    public void test03(@Autowired ChatClient chatClient){
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
    public void test04(@Autowired ChatClient chatClient, @Value("classpath:files/test.st") Resource resource){
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
}
