package com.mini_ecommerce.orders_service.repository;

import com.mini_ecommerce.orders_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
