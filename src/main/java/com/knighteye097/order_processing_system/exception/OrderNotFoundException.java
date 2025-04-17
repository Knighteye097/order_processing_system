package com.knighteye097.order_processing_system.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order with ID " + id + " not found.");
    }
}
