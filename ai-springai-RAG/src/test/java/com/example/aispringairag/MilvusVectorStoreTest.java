package com.example.aispringairag;


import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class MilvusVectorStoreTest {

    @Autowired
    @Qualifier("myVectorStore")
    private VectorStore milvusVectorStore;

    // 存储
    @Test
    public void test01() {
        List<String> batchTexts = new ArrayList<>();
        batchTexts.add("【2025新款】智能保温杯 316不锈钢 温度显示 24小时保温 白色 500ml");
        batchTexts.add("无线蓝牙耳机 半入耳式 降噪高清通话 续航40小时 适配安卓/苹果");
        batchTexts.add("机械键盘 青轴 104键 背光有线 电竞游戏专用 全键无冲 黑色");
        batchTexts.add("家用投影仪 1080P高清 自动对焦 5G双频WiFi 兼容4K 白色 便携款");
        batchTexts.add("电动牙刷 超声波清洁 5种模式 续航90天 软毛刷头 成人款 蓝色");

        List<Document> documents = batchTexts.stream()
                .map(Document::new)
                .collect(Collectors.toList());

        // 在这里将我们之前的文档直接调用wriete方法写入到miluvs中
        milvusVectorStore.write(documents);
    }

    // 查找
    @Test
    public void testQuery() {
        String query = "无线蓝牙耳机";
        List<Document> documents = milvusVectorStore.similaritySearch(query);
        for (Document document : documents) {
            System.out.println(document);
        }
    }

    // 元数据存储演示
// 在向量数据库中存储带元数据的文档
    @Test
    public void testMeteData() {
        // 准备第一批文档 - 商品类
        List<String> batchTexts = new ArrayList<>();
        batchTexts.add("【2025新款】智能保温杯 316不锈钢 温度显示 24小时保温 白色 500ml");
        batchTexts.add("无线蓝牙耳机 半入耳式 降噪高清通话 续航40小时 适配安卓/苹果");

        // 准备第二批文档 - 说明类
        List<String> batchTexts2 = new ArrayList<>();
        batchTexts2.add("机械键盘 青轴 104键 背光有线 电竞游戏专用 全键无冲 黑色");
        batchTexts2.add("家用投影仪 1080P高清 自动对焦 5G双频WiFi 兼容4K 白色 便携款");
        batchTexts2.add("电动牙刷 超声波清洁 5种模式 续航90天 软毛刷头 成人款 蓝色");

        // 使用Document.builder()创建带元数据的文档
        // metadata(String key, Object value)方法添加单个元数据
        List<Document> documents = batchTexts.stream()
                .map(document -> Document.builder()
                        .text(document)                    // 设置文档内容
                        .metadata("source", "商品")       // 添加source元数据
                        .build())
                .collect(Collectors.toList());

        List<Document> documents2 = batchTexts2.stream()
                .map(document -> Document.builder()
                        .text(document)
                        .metadata("source", "说明")       // 不同的分类
                        .build())
                .collect(Collectors.toList());

        // 写入向量数据库
        milvusVectorStore.write(documents);
        milvusVectorStore.write(documents2);
    }

    // 使用Map添加多个元数据
    @Test
    public void testWithMap() {
        String text = "【2025新款】智能保温杯 316不锈钢";

        // 创建包含多个元数据的Map
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "商品");
        metadata.put("category", "杯具");
        metadata.put("department", "家居用品");
        metadata.put("date", "2025-01-01");

        Document document = Document.builder()
                .text(text)
                .metadata(metadata)  // 一次性添加多个元数据
                .build();

        milvusVectorStore.write(List.of(document));
    }

    @Test
    public void testQueryWithMateData() {
        // 设置查询文本
        String query = "无线蓝牙耳机";

        // 使用SearchRequest.builder()构建检索请求
        // filterExpression()方法设置元数据过滤条件
        List<Document> documents = milvusVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)                              // 查询文本
                        .filterExpression("source=='商品'")       // 元数据过滤条件
                        .topK(10)                                 // 返回前10条结果
                        .build()
        );

        // 输出检索结果
        for (Document document : documents) {
            System.out.println(document);
        }
    }


    @Test
    public void testWithQuery() {
        // 场景1：多条件AND组合
        String query1 = "智能保温杯";
        List<Document> result1 = milvusVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query1)
                        .filterExpression("source=='商品' AND category=='杯具'")
                        .build()
        );

        // 场景2：OR组合
        String query2 = "键盘";
        List<Document> result2 = milvusVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query2)
                        .filterExpression("source=='商品' OR source=='说明'")
                        .build()
        );

        // 场景3：数值比较
        List<Document> result3 = milvusVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("电动牙刷")
                        .filterExpression("price > 50")  // 假设有price字段
                        .build()
        );
    }
}
