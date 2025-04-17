package com.knighteye097.order_processing_system.dto;

import com.knighteye097.order_processing_system.entity.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private List<ItemDto> items;
}
