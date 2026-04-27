package com.example.aispringairecord.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.aispringairecord.record.Order;
import com.example.aispringairecord.record.Product;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StructuredOutputConfig {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @Bean
    public BeanOutputConverter<Product> productBeanOutputConverter(){
        return new BeanOutputConverter<>(Product.class);
    }

    /**
     * 创建订单信息输出转换器
     * 用于处理嵌套Record的结构化输出
     */
    @Bean
    public BeanOutputConverter<Order> orderOutputConverter() {
        return new BeanOutputConverter<>(Order.class);
    }
}
