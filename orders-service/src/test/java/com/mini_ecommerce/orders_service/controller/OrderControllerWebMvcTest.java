package com.mini_ecommerce.orders_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini_ecommerce.orders_service.dto.OrderCreateRequest;
import com.mini_ecommerce.orders_service.dto.OrderResponse;
import com.mini_ecommerce.orders_service.dto.OrderUpdateRequest;
import com.mini_ecommerce.orders_service.service.OrderService;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createShouldReturn201() throws Exception {
        OrderResponse response = new OrderResponse();
        response.setId(10L);
        response.setUserId(3L);
        response.setProductId(7L);
        response.setSellerUserId(1L);
        response.setItemName("iPhone 13");
        response.setQuantity(2);
        response.setTotalPrice(new BigDecimal("1300.00"));
        response.setStatus("CREATED");
        response.setCreatedAt(Instant.parse("2026-02-11T20:00:00Z"));

        when(orderService.create(any(OrderCreateRequest.class))).thenReturn(response);

        String body = """
                {
                  "userId": 3,
                  "productId": 7,
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.itemName").value("iPhone 13"))
                .andExpect(jsonPath("$.totalPrice").value(1300.00));
    }

    @Test
    void updateShouldRejectMissingStatus() throws Exception {
        OrderUpdateRequest request = new OrderUpdateRequest();
        request.setQuantity(3);
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/orders/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
