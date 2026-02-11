package com.mini_ecommerce.products_service.service;

import com.mini_ecommerce.products_service.dto.ProductCreateRequest;
import com.mini_ecommerce.products_service.dto.ProductResponse;
import com.mini_ecommerce.products_service.dto.ProductUpdateRequest;
import com.mini_ecommerce.products_service.exception.NotFoundException;
import com.mini_ecommerce.products_service.model.Product;
import com.mini_ecommerce.products_service.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailable(request.getAvailable());
        product.setOwnerUserId(request.getOwnerUserId());
        return toResponse(productRepository.save(product));
    }

    public ProductResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getByOwner(Long ownerUserId) {
        return productRepository.findByOwnerUserId(ownerUserId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getEntity(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailable(request.getAvailable());
        product.setOwnerUserId(request.getOwnerUserId());
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = getEntity(id);
        productRepository.delete(product);
    }

    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found."));
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setAvailable(product.getAvailable());
        response.setOwnerUserId(product.getOwnerUserId());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}