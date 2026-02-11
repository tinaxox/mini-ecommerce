package com.mini_ecommerce.orders_service.service;

import com.mini_ecommerce.orders_service.client.ProductsClient;
import com.mini_ecommerce.orders_service.dto.ProductResponse;
import com.mini_ecommerce.orders_service.exception.NotFoundException;
import com.mini_ecommerce.orders_service.exception.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class ProductLookupService {

    private final ProductsClient productsClient;

    public ProductLookupService(ProductsClient productsClient) {
        this.productsClient = productsClient;
    }

    @CircuitBreaker(name = "products-service", fallbackMethod = "productFallback")
    @Retry(name = "products-service")
    public ProductResponse getProductOrThrow(Long productId) {
        return productsClient.getProductById(productId);
    }

    private ProductResponse productFallback(Long productId, Throwable ex) {
        if (ex instanceof FeignException feignException && feignException.status() == 404) {
            throw new NotFoundException("Product not found: " + productId);
        }
        throw new ServiceUnavailableException("Products service unavailable");
    }
}