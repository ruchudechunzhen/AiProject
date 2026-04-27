package com.example.aispringairecord.record;

import java.util.List;

public record Order(
        String orderId,
        List<OrderItem> orders,
        Double totalAmount,
        String status

) {}


