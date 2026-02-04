package org.example.productservice.service;

import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductService {

    Page<ProductResponse> findPageOfProducts(Pageable pageable);
    ProductResponse findProductById(Long id);
    ProductResponse createProduct(ProductRequest productRequest);
}
