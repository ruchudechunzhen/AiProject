package com.example.aispringairecord;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.aispringairecord.record.Product;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;


import java.util.List;

@SpringBootTest
public class RecordTest {


    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @Autowired
    private BeanOutputConverter<Product> productBeanOutputConverter;

    @Test
    public void testProductConverter(){
        String format = productBeanOutputConverter.getFormat();
        String promt = "请根据用户需求推荐一款合适的商品。\n" +
                "                用户需求：需要一款适合办公使用的电子设备\n" +
                "                \n" +
                "                请按照以下JSON格式返回结果：\n" +
                "                %s".formatted(format);
        ChatResponse response= dashScopeChatModel.call(new Prompt(promt));
        String jsonText = response.getResult().getOutput().getText();
        System.out.println("json结果"+jsonText);

        Product product = productBeanOutputConverter.convert(jsonText);
        System.out.println("转换后的Product对象：" + product);
        System.out.println("商品名称：" + product.name());
        System.out.println("商品价格：" + product.price());
        System.out.println("商品分类：" + product.category());
    }

    // 批量转化
    @Test
    public void testProductConverter02(){
        // 创建List<Product>的转换器
        BeanOutputConverter<List<Product>> listConverter =
                new BeanOutputConverter<>(
                        new ParameterizedTypeReference<List<Product>>() {}
                );

        String format = listConverter.getFormat();

        String prompt = """
            请推荐3款适合学生使用的电子产品。
            每款包含：名称、价格、分类
            
            返回格式：
            %s
            """.formatted(format);

        // 调用模型
        ChatResponse response = dashScopeChatModel.call(new Prompt(prompt));
        String jsonContent = response.getResult().getOutput().getText();

        // 转换为List<Product>
        List<Product> products = listConverter.convert(jsonContent);

        // 遍历输出
        products.forEach(p ->
                System.out.println("%s - ¥%s - %s".formatted(p.name(), p.price(), p.category()))
        );
    }

    @Test
    public void chatClient(){
        ChatClient client = ChatClient.builder(dashScopeChatModel).build();

        // 直接指定输出类型为Product
        // Spring AI会自动处理格式指令和转换
        Product product = client.prompt()
                .user(u -> u.text("推荐一款适合游戏的电脑"))
                .call()
                .entity(Product.class);
        System.out.println("商品名称：" + product.name());
        System.out.println("商品价格：" + product.price());
    }

    /**
     * 使用ChatClient输出List类型
     */
    @Test
    public void testChatClientListOutput() {
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel).build();

        // 使用ParameterizedTypeReference指定泛型类型
        List<Product> products = chatClient.prompt()
                .user("推荐3款无线蓝牙耳机")
                .call()
                .entity(new ParameterizedTypeReference<List<Product>>() {});

        products.forEach(System.out::println);
    }
}
