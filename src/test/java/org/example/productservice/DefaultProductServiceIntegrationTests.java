package org.example.productservice;


import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.enums.Category;
import org.example.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class DefaultProductServiceIntegrationTests {

    @Autowired
    private ProductService service;


    @Test
    void shouldCreateAndFindProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build();

        ProductResponse createdProduct = service.createProduct(request);
        ProductResponse foundProduct = service.findProductById(createdProduct.id());

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.name()).isEqualTo(request.name());
        assertThat(foundProduct.description()).isEqualTo(request.description());
        assertThat(foundProduct.price()).isEqualTo(request.price());
        assertThat(foundProduct.category()).isEqualTo(request.category());
        assertThat(foundProduct.amount()).isEqualTo(request.amount());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        assertThatThrownBy(() -> service.findProductById(99999L))
                .isInstanceOf(ProductNotFoundException.class);
    }


    @Test
    void shouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);

        service.createProduct(ProductRequest.builder()
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build());
        service.createProduct(ProductRequest.builder()
                        .name("test2")
                        .description("test-desc2")
                        .price(BigDecimal.TWO)
                        .category(Category.TV)
                        .amount(3)
                        .build());

        Page<ProductResponse> page = service.findPageOfProducts(pageable);


        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(ProductResponse::name)
                .containsExactly("test", "test2");
        assertThat(page.getContent()).extracting(ProductResponse::description)
                .containsExactly("test-desc", "test-desc2");
        assertThat(page.getContent()).extracting(ProductResponse::price)
                .containsExactly(BigDecimal.TEN, BigDecimal.TWO);
        assertThat(page.getContent()).extracting(ProductResponse::category)
                .containsExactly(Category.SMARTPHONES, Category.TV);
        assertThat(page.getContent()).extracting(ProductResponse::amount)
                .containsExactly(5, 3);
    }
}
