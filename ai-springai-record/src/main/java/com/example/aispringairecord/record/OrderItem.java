package com.example.aispringairecord.record;

public record OrderItem(
        String productName, // 商品名称
        Integer quantity, //购买数量
        Double subtotal //小计金额
) {
}
