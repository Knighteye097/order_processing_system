package com.knighteye097.order_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knighteye097.order_processing_system.dto.ItemDto;
import com.knighteye097.order_processing_system.dto.OrderRequest;
import com.knighteye097.order_processing_system.dto.OrderResponse;
import com.knighteye097.order_processing_system.entity.OrderStatus;
import com.knighteye097.order_processing_system.exception.GlobalExceptionHandler;
import com.knighteye097.order_processing_system.exception.OrderNotFoundException;
import com.knighteye097.order_processing_system.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(OrderController.class)
@Import({OrderControllerTest.MockOrderServiceConfig.class, GlobalExceptionHandler.class})
public class OrderControllerTest {

    @TestConfiguration
    static class MockOrderServiceConfig {
        @Bean
        public OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Positive: Create order with valid input returns 201")
        void createOrder_ValidInput_ReturnsCreated() throws Exception {
            OrderRequest orderRequest = new OrderRequest();
            ItemDto item = new ItemDto();
            item.setProductName("Test Item");
            item.setQuantity(1);
            List<ItemDto> items = Collections.singletonList(item);
            orderRequest.setItems(items);

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(1L);
            orderResponse.setCreatedAt(LocalDateTime.now());
            orderResponse.setStatus(OrderStatus.PENDING);
            orderResponse.setItems(items);

            Mockito.when(orderService.createOrder(Mockito.any(OrderRequest.class))).thenReturn(orderResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(1));
        }

        @Test
        @DisplayName("Negative: Create order with invalid input returns 400")
        void createOrder_InvalidInput_ReturnsBadRequest() throws Exception {
            // Sending an order with an empty items list should trigger validation.
            String emptyOrder = "{\"items\": []}";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyOrder))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Order By Id Tests")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Positive: Get existing order returns 200")
        void getOrderById_ExistingOrder_ReturnsOk() throws Exception {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(1L);
            orderResponse.setCreatedAt(LocalDateTime.now());
            orderResponse.setStatus(OrderStatus.PENDING);
            ItemDto item = new ItemDto();
            item.setProductName("Test Item");
            item.setQuantity(1);
            List<ItemDto> items = Collections.singletonList(item);
            orderResponse.setItems(items);

            Mockito.when(orderService.getOrderById(1L)).thenReturn(orderResponse);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(1));
        }

        @Test
        @DisplayName("Negative: Get non-existing order returns 404")
        void getOrderById_NonExistingOrder_ReturnsNotFound() throws Exception {
            Mockito.when(orderService.getOrderById(99L)).thenThrow(new OrderNotFoundException(99L));

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @DisplayName("Negative: Get order with invalid id format returns 400")
        void getOrderById_InvalidFormat_ReturnsBadRequest() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/abc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get All Orders Tests")
    class GetAllOrdersTests {

        @Test
        @DisplayName("Positive: Get all orders returns list with orders")
        void getAllOrders_WithOrders_ReturnsOk() throws Exception {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(1L);
            orderResponse.setCreatedAt(LocalDateTime.now());
            orderResponse.setStatus(OrderStatus.PENDING);
            ItemDto item = new ItemDto();
            item.setProductName("Test Item");
            item.setQuantity(1);
            List<ItemDto> items = Collections.singletonList(item);
            orderResponse.setItems(items);

            Mockito.when(orderService.getAllOrders(Optional.empty()))
                    .thenReturn(Collections.singletonList(orderResponse));

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderId").value(1));
        }

        @Test
        @DisplayName("Negative: Get all orders with filter returns empty list")
        void getAllOrders_NoOrders_ReturnsEmptyList() throws Exception {
            Mockito.when(orderService.getAllOrders(Optional.of(OrderStatus.SHIPPED)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                            .param("status", "SHIPPED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Update Order Status Tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Positive: Update existing order status returns 204")
        void updateOrderStatus_ExistingOrder_ReturnsNoContent() throws Exception {
            Mockito.doNothing().when(orderService).updateOrderStatus(1L, OrderStatus.SHIPPED);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/1")
                            .param("status", "SHIPPED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        @DisplayName("Negative: Update non-existing order status returns 404")
        void updateOrderStatus_NonExistingOrder_ReturnsNotFound() throws Exception {
            Mockito.doThrow(new OrderNotFoundException(99L))
                    .when(orderService).updateOrderStatus(99L, OrderStatus.SHIPPED);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/99")
                            .param("status", "SHIPPED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @DisplayName("Negative: Update order with invalid id format returns 400")
        void updateOrderStatus_InvalidFormat_ReturnsBadRequest() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/abc")
                            .param("status", "SHIPPED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @DisplayName("Negative: Update order with invalid enum value returns 400")
        void updateOrderStatus_InvalidEnumValue_ReturnsBadRequest() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/1")
                            .param("status", "INVALID")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Positive: Cancel existing order returns 204")
        void cancelOrder_ExistingOrder_ReturnsNoContent() throws Exception {
            Mockito.doNothing().when(orderService).cancelOrder(1L);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        @DisplayName("Negative: Cancel order in invalid state returns 400")
        void cancelOrder_InvalidCancellation_ReturnsBadRequest() throws Exception {
            Mockito.doThrow(new IllegalStateException("Only PENDING orders can be cancelled."))
                    .when(orderService).cancelOrder(2L);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @DisplayName("Negative: Cancel order with invalid id format returns 400")
        void cancelOrder_InvalidFormat_ReturnsBadRequest() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/abc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }
}
