package com.example.aispringairag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class EmbeddingModelTest {


    @Autowired
    private DashScopeEmbeddingModel embeddingModel;

    @Test
    public void testEmbeddingModel(){
        String text = "将这段的对话通过大模型向量化";
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(text));
        float[] output = embeddingResponse.getResult().getOutput();
        System.out.println("向量维度：" + output.length);
        // 输出向量内容（前10维，便于查看）
        System.out.println("向量前10维：" + Arrays.toString(Arrays.copyOf(output, 10)));
    }

    @Test
    public void testBatchEmbeddingModel(){
        // 1. 准备批量测试数据
        // 模拟电商RAG场景中的商品信息批量向量化
        List<String> batchTexts = new ArrayList<>();
        batchTexts.add("【2025新款】智能保温杯 316不锈钢 温度显示 24小时保温 白色 500ml");
        batchTexts.add("无线蓝牙耳机 半入耳式 降噪高清通话 续航40小时 适配安卓/苹果");
        batchTexts.add("机械键盘 青轴 104键 背光有线 电竞游戏专用 全键无冲 黑色");
        batchTexts.add("家用投影仪 1080P高清 自动对焦 5G双频WiFi 兼容4K 白色 便携款");
        batchTexts.add("电动牙刷 超声波清洁 5种模式 续航90天 软毛刷头 成人款 蓝色");

        // 2. 执行批量向量化
        // 一次性将5条文本转换为向量
        // 内部会进行批量API调用，相比单条调用效率提升显著
        EmbeddingResponse batchResponse = embeddingModel.embedForResponse(batchTexts);

        // 3. 处理返回结果
        // getResults()返回Iterator，便于遍历多条结果
        batchResponse.getResults().iterator().forEachRemaining(result -> {
            float[] embedding = result.getOutput();
            System.out.println("向量维度：" + embedding.length);
            System.out.println("向量前10维：" + Arrays.toString(Arrays.copyOf(embedding, 10)));
        });
    }


}
