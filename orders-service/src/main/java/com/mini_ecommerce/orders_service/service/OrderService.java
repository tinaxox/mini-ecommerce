package com.mini_ecommerce.orders_service.service;

import com.mini_ecommerce.orders_service.dto.OrderCreateRequest;
import com.mini_ecommerce.orders_service.dto.OrderDetailsResponse;
import com.mini_ecommerce.orders_service.dto.OrderResponse;
import com.mini_ecommerce.orders_service.dto.OrderUpdateRequest;
import com.mini_ecommerce.orders_service.dto.ProductResponse;
import com.mini_ecommerce.orders_service.dto.SellerOrderNotificationResponse;
import com.mini_ecommerce.orders_service.dto.UserResponse;
import com.mini_ecommerce.orders_service.exception.BadRequestException;
import com.mini_ecommerce.orders_service.exception.NotFoundException;
import com.mini_ecommerce.orders_service.model.Order;
import com.mini_ecommerce.orders_service.repository.OrderRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final String DEFAULT_STATUS = "CREATED";

    private final OrderRepository orderRepository;
    private final Validator validator;
    private final UserLookupService userLookupService;
    private final ProductLookupService productLookupService;

    public OrderService(OrderRepository orderRepository,
                        Validator validator,
                        UserLookupService userLookupService,
                        ProductLookupService productLookupService) {
        this.orderRepository = orderRepository;
        this.validator = validator;
        this.userLookupService = userLookupService;
        this.productLookupService = productLookupService;
    }

    public OrderResponse create(OrderCreateRequest request) {
        userLookupService.getUserOrThrow(request.getUserId());
        ProductResponse product = productLookupService.getProductOrThrow(request.getProductId());

        if (!Boolean.TRUE.equals(product.getAvailable())) {
            throw new BadRequestException("Product is not available for ordering: " + request.getProductId());
        }

        if (request.getUserId().equals(product.getOwnerUserId())) {
            throw new BadRequestException("User cannot order their own product.");
        }

        String status = request.getStatus() == null || request.getStatus().isBlank()
                ? DEFAULT_STATUS
                : request.getStatus();

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order(
                request.getUserId(),
                request.getProductId(),
                product.getOwnerUserId(),
                product.getName(),
                request.getQuantity(),
                totalPrice,
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
        UserResponse buyer = userLookupService.getUserOrThrow(order.getUserId());
        UserResponse seller = userLookupService.getUserOrThrow(order.getSellerUserId());
        ProductResponse product = productLookupService.getProductOrThrow(order.getProductId());

        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setOrder(order);
        response.setBuyer(buyer);
        response.setSeller(seller);
        response.setProduct(product);
        return response;
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SellerOrderNotificationResponse> getSellerNotifications(Long sellerUserId) {
        return orderRepository.findBySellerUserIdOrderByCreatedAtDesc(sellerUserId).stream()
                .limit(20)
                .map(this::toSellerNotification)
                .collect(Collectors.toList());
    }

    public OrderResponse update(Long id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        if (!request.getUnknownFields().isEmpty()) {
            throw new BadRequestException("Unsupported fields for order update: " + request.getUnknownFields().keySet());
        }

        validateRequest(request);

        BigDecimal unitPrice = resolveUnitPrice(order);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())).setScale(2, RoundingMode.HALF_UP));
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
        response.setProductId(order.getProductId());
        response.setSellerUserId(order.getSellerUserId());
        response.setItemName(order.getItemName());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    private SellerOrderNotificationResponse toSellerNotification(Order order) {
        SellerOrderNotificationResponse response = new SellerOrderNotificationResponse();
        response.setOrderId(order.getId());
        response.setProductId(order.getProductId());
        response.setBuyerUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setMessage("Your product '" + order.getItemName() + "' has been ordered.");
        return response;
    }

    private BigDecimal resolveUnitPrice(Order order) {
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            throw new BadRequestException("Order has invalid quantity and cannot be repriced.");
        }
        return order.getTotalPrice().divide(BigDecimal.valueOf(order.getQuantity()), 4, RoundingMode.HALF_UP);
    }

    private void validateRequest(OrderUpdateRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
