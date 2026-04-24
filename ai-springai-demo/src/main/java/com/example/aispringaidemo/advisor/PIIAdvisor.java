package com.example.aispringaidemo.advisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PIIAdvisor implements BaseAdvisor {

    @Autowired
    @Qualifier("PIIChatClient")
    private ChatClient PIIChatClient;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        UserMessage userMessage = chatClientRequest.prompt().getUserMessage();
        System.out.println("PIIAdvisor截获用户信息"+userMessage.getText());

        // 使用PII模型对用户数据脱敏
        String cleanResult = PIIChatClient.prompt()
                .user("请对以下数据脱敏" + userMessage.getText())
                .call()
                .content();
        System.out.println("脱敏结果"+cleanResult);
        // 将脱敏后的数据重新封装到用户信息中
        return chatClientRequest
                .mutate()
                .prompt(Prompt.builder().content(cleanResult).messages().build())
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
