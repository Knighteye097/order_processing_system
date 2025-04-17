package com.knighteye097.order_processing_system.service;

import com.knighteye097.order_processing_system.dto.OrderRequest;
import com.knighteye097.order_processing_system.dto.OrderResponse;
import com.knighteye097.order_processing_system.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing orders.
 * Provides methods to create, retrieve, update, and cancel orders, as well as to update pending orders to processing.
 */
public interface OrderService {

    /**
     * Creates a new order based on the given request.
     *
     * @param request the order request details
     * @return an OrderResponse representing the created order
     */
    OrderResponse createOrder(OrderRequest request);

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return an OrderResponse representing the matching order
     */
    OrderResponse getOrderById(Long orderId);

    /**
     * Retrieves all orders, optionally filtered by order status.
     *
     * @param status an Optional filter for the order status
     * @return a list of OrderResponse objects representing the orders
     */
    List<OrderResponse> getAllOrders(Optional<OrderStatus> status);

    /**
     * Updates the status of the order identified by the given ID.
     *
     * @param orderId the ID of the order to update
     * @param status the new order status to set
     */
    void updateOrderStatus(Long orderId, OrderStatus status);

    /**
     * Cancels the order identified by the given ID.
     *
     * @param orderId the ID of the order to cancel
     */
    void cancelOrder(Long orderId);

    /**
     * Updates all orders with a status of PENDING to PROCESSING.
     */
    void updatePendingOrdersToProcessing();
}
