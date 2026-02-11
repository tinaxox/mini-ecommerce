package com.mini_ecommerce.orders_service.dto;

public class OrderDetailsResponse {

    private OrderResponse order;
    private UserResponse buyer;
    private UserResponse seller;
    private ProductResponse product;

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public UserResponse getBuyer() {
        return buyer;
    }

    public void setBuyer(UserResponse buyer) {
        this.buyer = buyer;
    }

    public UserResponse getSeller() {
        return seller;
    }

    public void setSeller(UserResponse seller) {
        this.seller = seller;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }
}