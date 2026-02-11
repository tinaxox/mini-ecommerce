package com.mini_ecommerce.products_service.repository;

import com.mini_ecommerce.products_service.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByOwnerUserId(Long ownerUserId);
}