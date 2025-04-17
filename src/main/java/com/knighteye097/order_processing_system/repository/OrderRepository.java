package com.knighteye097.order_processing_system.repository;

import com.knighteye097.order_processing_system.entity.Order;
import com.knighteye097.order_processing_system.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}