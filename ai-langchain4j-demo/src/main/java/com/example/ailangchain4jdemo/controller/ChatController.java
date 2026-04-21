package com.example.ailangchain4jdemo.controller;

import com.example.ailangchain4jdemo.config.AiConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final QwenChatModel qwenChatModel;

    private final QwenStreamingChatModel qwenStreamingChatModel;

    private final AiConfig.Assistant aiAssistant;

    @RequestMapping("/chat")
    public String QWenChat(@RequestParam("question") String questtion){
        return qwenChatModel.chat(questtion);
    }

    @RequestMapping(value = "/streamChat" , produces = "text/stream;charset=UTF-8")
    public Flux<String> QWenStreamingChat(@RequestParam("question") String questtion){
        // 流式响应需要用Flux进行返回
        Flux<String> flux = Flux.create( stringFluxSink -> {
            // 使用chat方法流式调用模型，返回的内容需要使用StreamingChatResponseHandler来接收
            qwenStreamingChatModel.chat(questtion, new StreamingChatResponseHandler() {

                @Override
                public void onPartialResponse(String partialResponse) {
                    // 流式返回的内容
                    stringFluxSink.next(partialResponse);
                }
                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    // 结束的时候需要主动的关闭流式响应的连接
                    stringFluxSink.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    // 流式响应出现异常时触发的方法
                    throwable.printStackTrace();
                    stringFluxSink.error(throwable);
                }
            });
        });
        return flux;
    }


    @RequestMapping("/memoryChat")
    public String memoryChat(@RequestParam(value = "question", defaultValue = "我叫千问") String question) {
        return aiAssistant.chat(question);
    }

    @RequestMapping(value = "/memoryStreamChat" , produces = "text/stream;charset=UTF-8")
    public Flux<String> QWenMemoryStreamingChat(@RequestParam("question") String question){

        // 3. 调用AI助手的流式对话方法，获取LangChain4j的TokenStream（AI的流式响应对象）
        TokenStream stream = aiAssistant.stream(question);

        // 4. 将TokenStream转换成Spring WebFlux的Flux<String>（核心转换逻辑）
        return Flux.create( sink -> {
            // 4.1 AI返回单个字符/片段时，把内容发送到Flux流中（前端能实时收到）
            stream.onPartialResponse(sink::next)
                    // 4.2 AI响应完全结束时，关闭Flux流
                    .onCompleteResponse(chatResponse -> sink.complete())
                    // 4.3 发生错误时，把错误传给Flux的错误处理（前端能捕获）
                    .onError(sink::error)
                    // 4.4 启动AI的流式响应（开始接收AI的逐字回复）
                    .start();
        });
    }
}
