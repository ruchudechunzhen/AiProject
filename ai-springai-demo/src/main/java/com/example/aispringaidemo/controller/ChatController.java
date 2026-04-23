package com.example.aispringaidemo.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/springAi")
@RequiredArgsConstructor
public class ChatController {

    private final DashScopeChatModel dashScopeChatModel;

    @RequestMapping(value = "/dashChat",produces = "text/stream;charset=UTF-8")
    public Flux<String> dashChat(@RequestParam("question") String question){
        return dashScopeChatModel.stream(question);
    }
}
