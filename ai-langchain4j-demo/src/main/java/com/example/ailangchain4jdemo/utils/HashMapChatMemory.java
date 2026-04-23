package com.example.ailangchain4jdemo.utils;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class HashMapChatMemory implements ChatMemory {

    // 全局存储：key=memoryId，value=该会话的消息队列
    private static final ConcurrentHashMap<String, Deque<ChatMessage>>  GLOBAL_MEMORY_STORE = new ConcurrentHashMap<>();

    // 每个会话的最大消息数（对应原 maxMessages(10)）
    private final int maxMessage;

    // 当前会话的 memoryId
    private final String memoryId;

    public HashMapChatMemory(int maxMessage, String memoryId) {
        this.maxMessage = maxMessage;
        this.memoryId = memoryId;
        // 初始化：如果该 memoryId 没有消息队列，创建一个空队列
        GLOBAL_MEMORY_STORE.putIfAbsent(memoryId, new LinkedBlockingDeque<>());
    }

    private Deque<ChatMessage> getMessageQueue(){
        return GLOBAL_MEMORY_STORE.get(memoryId);
    }

    @Override
    public Object id() {
        return this.memoryId;
    }

    @Override
    public void add(ChatMessage message) {
        Deque<ChatMessage> messageQueue = getMessageQueue();
        messageQueue.addLast(message);
        // 循环删除，知道符合数量
        while (messageQueue.size() > maxMessage){
            messageQueue.removeFirst();
        }
    }

    @Override
    public List<ChatMessage> messages() {
        return List.of();
    }

    @Override
    public void clear() {
        getMessageQueue().clear();
    }
}
