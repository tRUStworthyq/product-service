package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findPageOfProducts(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long id) {
        Optional<Product> product = repository.findById(id);
        if (product.isEmpty()) {
            throw new ProductNotFoundException(id);
        }
        return toResponse(product.get());
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .category(productRequest.category())
                .amount(productRequest.amount())
                .build();

        Product savedProduct = repository.save(product);
        return toResponse(savedProduct);
    }


    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .amount(product.getAmount())
                .build();
    }
}
