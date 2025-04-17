package com.knighteye097.order_processing_system.scheduler;

import com.knighteye097.order_processing_system.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusScheduler {

    private final OrderService orderService;

    public OrderStatusScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    // Every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void processPendingOrders() {
        orderService.updatePendingOrdersToProcessing();
    }
}
