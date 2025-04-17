package com.knighteye097.order_processing_system.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
public class OrderRequest {
    @NotEmpty(message = "Order must contain at least one item")
    private List<ItemDto> items;
}