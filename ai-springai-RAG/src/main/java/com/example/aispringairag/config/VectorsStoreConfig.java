package com.example.aispringairag.config;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import io.milvus.client.MilvusServiceClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorsStoreConfig {


    @Autowired
    private DashScopeEmbeddingModel dashScopeEmbeddingModel;

    @Autowired
    private MilvusServiceClient milvusServiceClient;



    @Bean(name = "myVectorStore")
    public VectorStore vectorStore() {
        // 将miluvs的客户端和想要使用的向量模型组合成为一个VectorStore注入到Spring容器中
        return MilvusVectorStore.builder(milvusServiceClient, dashScopeEmbeddingModel).build();
    }
}
