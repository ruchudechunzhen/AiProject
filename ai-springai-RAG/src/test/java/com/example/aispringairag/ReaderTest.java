package com.example.aispringairag;


import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

@SpringBootTest
public class ReaderTest {

    @Value("classpath:files/测试pdf.pdf")
    private Resource pdfResource;

    @Value("classpath:files/测试word.doc")
    private Resource wordResource;

    @Test
    public void testPdf() {
        // PagePdfDocumentReader初始化时接收一个Resource对象
        // Resource是Spring框架的资源抽象，表示文件或网络资源
        // @Value注解会自动注入classpath下的文件资源
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);

        // read()方法继承自DocumentReader接口
        // 返回List<Document>，每个Document代表PDF的一页内容
        for (Document document : reader.read()) {
            System.out.println(document);
        }
    }

    @Test
    public void test02(){
        // TikaDocumentReader的构造方式与PagePdfDocumentReader相同
        // 接收Spring的Resource对象作为参数
        TikaDocumentReader reader = new TikaDocumentReader(wordResource);

        // read()方法返回List<Document>
        // 每个Document包含提取出的文本内容和元数据
        for (Document document : reader.read()) {
            System.out.println(document);
        }
    }
}
