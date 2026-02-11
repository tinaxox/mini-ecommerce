package com.mini_ecommerce.orders_service.dto;

public class OrderDetailsResponse {

    private OrderResponse order;
    private UserResponse user;

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
