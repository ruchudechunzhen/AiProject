package com.example.aispringairag;

import com.alibaba.cloud.ai.transformer.splitter.RecursiveCharacterTextSplitter;
import com.alibaba.cloud.ai.transformer.splitter.SentenceSplitter;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class SplitterTest {

    @Value("classpath:files/测试pdf.pdf")
    private Resource pdfResource;

    @Value("classpath:files/测试word.doc")
    private Resource wordResource;



    // 文档分割器演示 - Token拆分方式
    @Test
    public void testToken() {
        //Token拆分器 是按照token的长度进行拆分
        //问题：造成语义割裂
        List<Document> documents = new PagePdfDocumentReader(pdfResource).read();

        //创建Token拆分器
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(200, 100, 5, 10000, true);

        List<Document> apply = tokenTextSplitter.apply(documents);
        System.out.println("原始读取的文档数："+documents.size()+"===="+"拆分后的文档数"+apply.size());
        apply.forEach(System.out::println);
    }

    // 文档分割器演示 - Token拆分方式
    // SentenceSplitter 文档分割器演示
// 语义拆分：按句子进行分割，保持语义完整性
    @Test
    public void testSentenceSplitter() {
        // 第一步：读取PDF文档
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
        List<Document> documents = reader.read();

        // 第二步：创建SentenceSplitter语义分割器
        // 参数maxLength表示每个块的最大字符数
        // 当一个句子超过这个长度时，会进行拆分
        SentenceSplitter sentenceSplitter = new SentenceSplitter(512);

        // 第三步：执行语义分割
        List<Document> apply = sentenceSplitter.apply(documents);

        System.out.println("读取文档数：" + documents.size() + "，分割后文档数：" + apply.size());
        apply.forEach(System.out::println);
    }


    // RecursiveCharacterTextSplitter 文档分割器演示
// 递归拆分：按分隔符优先级递归拆分，是最常用的中文分割方案
    @Test
    public void testRecursiveCharacterTextSplitter() {
        // 第一步：读取PDF文档
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
        List<Document> documents = reader.read();

        // 第二步：创建RecursiveCharacterTextSplitter递归分割器
        // 使用默认配置创建分割器
        // 会自动使用中英文分隔符进行递归拆分
        RecursiveCharacterTextSplitter splitter = new RecursiveCharacterTextSplitter();

        // 第三步：执行递归拆分
        List<Document> apply = splitter.apply(documents);

        System.out.println("读取文档数：" + documents.size() + "，分割后文档数：" + apply.size());
        apply.forEach(System.out::println);
    }

}
