package com.example.aispringaitoolcalling;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolTest {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    @Qualifier("chatClientReflect")
    private ChatClient chatClientReflect;

    @Test
    public void testTool(){
        String questtion = "查询北京天气";
        String content = chatClient.prompt()
                .user(questtion)
                .call()
                .content();
        System.out.println(content);
    }

    // 通过反射调用，这样对业务代码没有任务侵入
    @Test
    public void testCallBack() {
        // 提出的问题涉及时间信息
        String question = "现在的时间是多少";

        String content = chatClientReflect.prompt()
                .user(question)
                .call()
                .content();

        System.out.println("MethodToolCallback 方法调用工具结果：\n" + content);
    }


}
