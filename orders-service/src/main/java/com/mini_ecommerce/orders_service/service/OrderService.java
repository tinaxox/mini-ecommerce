package com.mini_ecommerce.orders_service.service;

import com.mini_ecommerce.orders_service.dto.OrderCreateRequest;
import com.mini_ecommerce.orders_service.dto.OrderDetailsResponse;
import com.mini_ecommerce.orders_service.dto.OrderResponse;
import com.mini_ecommerce.orders_service.dto.OrderUpdateRequest;
import com.mini_ecommerce.orders_service.dto.UserResponse;
import com.mini_ecommerce.orders_service.exception.NotFoundException;
import com.mini_ecommerce.orders_service.model.Order;
import com.mini_ecommerce.orders_service.repository.OrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final String DEFAULT_STATUS = "CREATED";

    private final OrderRepository orderRepository;
    private final Validator validator;
    private final UserLookupService userLookupService;

    public OrderService(OrderRepository orderRepository, Validator validator, UserLookupService userLookupService) {
        this.orderRepository = orderRepository;
        this.validator = validator;
        this.userLookupService = userLookupService;
    }

    public OrderResponse create(OrderCreateRequest request) {
        userLookupService.getUserOrThrow(request.getUserId());
        String status = request.getStatus() == null || request.getStatus().isBlank()
                ? DEFAULT_STATUS
                : request.getStatus();

        Order order = new Order(
                request.getUserId(),
                request.getItemName(),
                request.getQuantity(),
                request.getTotalPrice(),
                status,
                Instant.now()
        );

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return toResponse(order);
    }

    public OrderDetailsResponse getDetails(Long id) {
        OrderResponse order = getById(id);
        UserResponse user = userLookupService.getUserOrThrow(order.getUserId());
        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setOrder(order);
        response.setUser(user);
        return response;
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse update(Long id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        validateRequest(request);

        order.setItemName(request.getItemName());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(request.getStatus());

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setItemName(order.getItemName());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    private void validateRequest(OrderUpdateRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
