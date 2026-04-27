package com.example.aispringaitoolcalling.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.aispringaitoolcalling.service.ToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@Configuration
public class ChatClientConfig {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;
    @Autowired
    private ToolService toolService;

    @Bean("chatClientTool")
    public ChatClient chatClient(){
        return ChatClient.builder(dashScopeChatModel)
                .defaultTools(toolService)
                .build();
    }

    @Bean("chatClientReflect")
    public ChatClient chatClientReflect(){
        Method method = ReflectionUtils.findMethod(ToolService.class,"getCurrentDateTime");

        MethodToolCallback methodToolCallback = MethodToolCallback.builder()
                .toolDefinition(
                        ToolDefinitions.builder(method)
                                .name("get_current_time") //定义工具名称
                                .description("获取当前的系统时间") //定义工具描述
                                .build()
                )
                // 指定要调用的方法
                .toolMethod(method)
                // 指定包含该方法的对象实例
                .toolObject(toolService)
                .build();
        return ChatClient.builder(dashScopeChatModel)
                .defaultToolCallbacks(methodToolCallback)
                .build();
    }
}
