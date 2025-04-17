package com.knighteye097.order_processing_system.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String productName;
    private int quantity;
}
