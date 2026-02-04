package org.example.productservice;

import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.entity.Product;
import org.example.productservice.model.enums.Category;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.impl.DefaultProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultProductServiceTests {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private DefaultProductService service;

    @Test
    void findProductById_whenProductExists_shouldReturnsProductResponse() {
        Long id = 1L;

        Product product = Product.builder()
                .id(id)
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(product));

        ProductResponse response = service.findProductById(id);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo(product.getName());
        assertThat(response.description()).isEqualTo(product.getDescription());
        assertThat(response.price()).isEqualTo(product.getPrice());
        assertThat(response.category()).isEqualTo(product.getCategory());
        assertThat(response.amount()).isEqualTo(product.getAmount());
        verify(repository, times(1)).findById(any(Long.class));
    }

    @Test
    void findProductById_whenProductNotFound_shouldThrowException() {
        Long nonExistenceId = 999L;

        when(repository.findById(nonExistenceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findProductById(nonExistenceId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(nonExistenceId.toString());
    }

    @Test
    void createProduct_withValidRequest_shouldSaveAndReturnNewProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .amount(request.amount())
                .build();

        when(repository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = service.createProduct(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(savedProduct.getId());
        assertThat(response.name()).isEqualTo(savedProduct.getName());
        assertThat(response.description()).isEqualTo(savedProduct.getDescription());
        assertThat(response.price()).isEqualTo(savedProduct.getPrice());
        assertThat(response.category()).isEqualTo(savedProduct.getCategory());
        assertThat(response.amount()).isEqualTo(savedProduct.getAmount());
        verify(repository, times(1)).save(any(Product.class));
    }


    @Test
    void findPageOfProducts_withValidRequest_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage = new PageImpl<>(List.of(
                Product.builder()
                        .id(1L)
                        .name("test")
                        .description("test-desc")
                        .price(BigDecimal.TEN)
                        .category(Category.SMARTPHONES)
                        .amount(5)
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("test2")
                        .description("test-desc2")
                        .price(BigDecimal.TWO)
                        .category(Category.TV)
                        .amount(3)
                        .build()
        ));

        when(repository.findAll(pageable)).thenReturn(productPage);

        Page<ProductResponse> result = service.findPageOfProducts(pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).extracting(ProductResponse::id)
                .containsExactly(1L, 2L);
        assertThat(result.getContent()).extracting(ProductResponse::name)
                .containsExactly("test", "test2");
        assertThat(result.getContent()).extracting(ProductResponse::description)
                .containsExactly("test-desc", "test-desc2");
        assertThat(result.getContent()).extracting(ProductResponse::price)
                .containsExactly(BigDecimal.TEN, BigDecimal.TWO);
        assertThat(result.getContent()).extracting(ProductResponse::category)
                .containsExactly(Category.SMARTPHONES, Category.TV);
        assertThat(result.getContent()).extracting(ProductResponse::amount)
                .containsExactly(5, 3);
        verify(repository, times(1)).findAll(pageable);


    }

    @Test
    void findPageOfProducts_withEmptyData_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage = new PageImpl<>(List.of());

        when(repository.findAll(pageable)).thenReturn(productPage);

        Page<ProductResponse> result = service.findPageOfProducts(pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(0);
        verify(repository, times(1)).findAll(pageable);
    }
}
