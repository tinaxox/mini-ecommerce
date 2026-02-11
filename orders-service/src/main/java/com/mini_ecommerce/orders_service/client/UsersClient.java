package com.mini_ecommerce.orders_service.client;

import com.mini_ecommerce.orders_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service")
public interface UsersClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
