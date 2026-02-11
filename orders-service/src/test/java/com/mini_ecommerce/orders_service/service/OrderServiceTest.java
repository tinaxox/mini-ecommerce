package com.mini_ecommerce.orders_service.service;

import com.mini_ecommerce.orders_service.dto.OrderUpdateRequest;
import com.mini_ecommerce.orders_service.exception.BadRequestException;
import com.mini_ecommerce.orders_service.model.Order;
import com.mini_ecommerce.orders_service.repository.OrderRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private ProductLookupService productLookupService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        orderService = new OrderService(orderRepository, validator, userLookupService, productLookupService);
    }

    @Test
    void updateShouldRejectUnknownFields() {
        Order existing = new Order(3L, 7L, 1L, "iPhone 13", 2, new BigDecimal("1300.00"), "CREATED", Instant.now());
        existing.setId(10L);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));

        OrderUpdateRequest request = new OrderUpdateRequest();
        request.setQuantity(3);
        request.setStatus("SHIPPED");
        request.captureUnknownField("totalPrice", 999.99);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> orderService.update(10L, request));
        assertEquals("Unsupported fields for order update: [totalPrice]", ex.getMessage());
    }

    @Test
    void updateShouldRecalculateTotalPriceUsingStoredUnitPrice() {
        Order existing = new Order(3L, 7L, 1L, "iPhone 13", 2, new BigDecimal("1300.00"), "CREATED", Instant.now());
        existing.setId(10L);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderUpdateRequest request = new OrderUpdateRequest();
        request.setQuantity(3);
        request.setStatus("SHIPPED");

        var updated = orderService.update(10L, request);
        assertEquals(new BigDecimal("1950.00"), updated.getTotalPrice());
        assertEquals("SHIPPED", updated.getStatus());
        assertEquals(3, updated.getQuantity());
    }
}
