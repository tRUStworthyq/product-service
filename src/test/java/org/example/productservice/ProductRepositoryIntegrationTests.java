package org.example.productservice;

import org.example.productservice.model.entity.Product;
import org.example.productservice.model.enums.Category;
import org.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class ProductRepositoryIntegrationTests {

    @Autowired
    private ProductRepository repository;

    @AfterEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveAndFindProduct() {
        Product product = Product.builder()
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build();

        Product savedProduct = repository.save(product);

        Optional<Product> foundProduct = repository.findById(savedProduct.getId());

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.get().getName()).isEqualTo(product.getName());
        assertThat(foundProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(foundProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(foundProduct.get().getCategory()).isEqualTo(product.getCategory());
        assertThat(foundProduct.get().getAmount()).isEqualTo(product.getAmount());
    }

    @Test
    void shouldFindPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product1 = Product.builder()
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build();
        Product product2 = Product.builder()
                .name("test2")
                .description("test-desc2")
                .price(BigDecimal.TWO)
                .category(Category.TV)
                .amount(3)
                .build();

        repository.save(product1);
        repository.save(product2);


        Page<Product> productPage = repository.findAll(pageable);

        assertThat(productPage).isNotNull();
        assertThat(productPage.getTotalElements()).isEqualTo(2);
        assertThat(productPage.getContent()).extracting(Product::getName)
                .containsExactly("test", "test2");
        assertThat(productPage.getContent()).extracting(Product::getDescription)
                .containsExactly("test-desc", "test-desc2");
        assertThat(productPage.getContent()).extracting(Product::getPrice)
                .containsExactly(BigDecimal.TEN, BigDecimal.TWO);
        assertThat(productPage.getContent()).extracting(Product::getCategory)
                .containsExactly(Category.SMARTPHONES, Category.TV);
        assertThat(productPage.getContent()).extracting(Product::getAmount)
                .containsExactly(5, 3);
    }
}
