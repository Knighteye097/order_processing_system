package com.knighteye097.order_processing_system.service.impl;

import com.knighteye097.order_processing_system.dto.ItemDto;
import com.knighteye097.order_processing_system.dto.OrderRequest;
import com.knighteye097.order_processing_system.dto.OrderResponse;
import com.knighteye097.order_processing_system.entity.Order;
import com.knighteye097.order_processing_system.entity.OrderItem;
import com.knighteye097.order_processing_system.entity.OrderStatus;
import com.knighteye097.order_processing_system.exception.OrderNotFoundException;
import com.knighteye097.order_processing_system.repository.OrderRepository;
import com.knighteye097.order_processing_system.service.OrderService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> new OrderItem(itemRequest.getProductName(), itemRequest.getQuantity()))
                .toList();

        items.forEach(item -> item.setOrder(order)); // Set order in each item
        order.setItems(items);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(Optional<OrderStatus> status) {
        List<Order> orders = status.map(orderRepository::findByStatus)
                .orElseGet(orderRepository::findAll);
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public void updatePendingOrdersToProcessing() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        pendingOrders.forEach(order -> order.setStatus(OrderStatus.PROCESSING));
        orderRepository.saveAll(pendingOrders);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setItems(
                order.getItems().stream()
                        .map(item -> new ItemDto(item.getProductName(), item.getQuantity()))
                        .collect(Collectors.toList())
        );
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}