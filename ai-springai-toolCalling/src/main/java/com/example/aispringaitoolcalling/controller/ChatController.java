package com.example.aispringaitoolcalling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    @Qualifier("chatClientTool")
    private ChatClient chatClient;
    /**
     * 对话接口
     *
     * 使用HTTP Basic认证保护
     * 调用时会携带请求用户的身份信息
     *
     * @param question 用户问题
     * @return AI回答内容
     */
    @GetMapping("/call")
    public String call(@RequestParam String question) {
        String content = chatClient.prompt()
                .user(question)
                .call()
                .content();
        return content;
    }

}
