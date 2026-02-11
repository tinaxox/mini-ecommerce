package com.mini_ecommerce.orders_service.service;

import com.mini_ecommerce.orders_service.client.UsersClient;
import com.mini_ecommerce.orders_service.dto.UserResponse;
import com.mini_ecommerce.orders_service.exception.NotFoundException;
import com.mini_ecommerce.orders_service.exception.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class UserLookupService {

    private final UsersClient usersClient;

    public UserLookupService(UsersClient usersClient) {
        this.usersClient = usersClient;
    }

    @CircuitBreaker(name = "users-service", fallbackMethod = "userFallback")
    @Retry(name = "users-service")
    public UserResponse getUserOrThrow(Long userId) {
        return usersClient.getUserById(userId);
    }

    private UserResponse userFallback(Long userId, Throwable ex) {
        if (ex instanceof FeignException feignException && feignException.status() == 404) {
            throw new NotFoundException("User not found: " + userId);
        }
        throw new ServiceUnavailableException("Users service unavailable");
    }
}
