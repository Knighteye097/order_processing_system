package com.knighteye097.order_processing_system.controller;

import com.knighteye097.order_processing_system.dto.OrderRequest;
import com.knighteye097.order_processing_system.dto.OrderResponse;
import com.knighteye097.order_processing_system.entity.OrderStatus;
import com.knighteye097.order_processing_system.service.OrderService;
import com.knighteye097.order_processing_system.validation.EnumValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID")
            @PathVariable
            @Pattern(regexp = "\\d+", message = "OrderId must be a valid number") String orderId) {
        Long id = Long.valueOf(orderId);
        OrderResponse order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "List all orders, optionally filtered by status")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @Parameter(description = "Filter by order status")
            @RequestParam Optional<OrderStatus> status) {
        List<OrderResponse> orders = orderService.getAllOrders(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "Order ID")
            @PathVariable
            @Pattern(regexp = "\\d+", message = "OrderId must be a valid number") String orderId,
            @Parameter(description = "New order status")
            @RequestParam
            @EnumValidator(enumClass = OrderStatus.class, message = "Invalid order status. Allowed values: PENDING, SHIPPED") String status) {
        Long id = Long.valueOf(orderId);
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        orderService.updateOrderStatus(id, orderStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order ID")
            @PathVariable
            @Pattern(regexp = "\\d+", message = "OrderId must be a valid number") String orderId) {
        Long id = Long.valueOf(orderId);
        orderService.cancelOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}