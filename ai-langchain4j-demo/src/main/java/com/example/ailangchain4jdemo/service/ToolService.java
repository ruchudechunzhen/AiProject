package com.example.ailangchain4jdemo.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Tool("姓名查找用户数量")
    public Integer nameCount(@P("name") String name){
        System.out.println("姓名:"+name);
        return 20;
    }
}
