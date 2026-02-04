package org.example.productservice;

import org.example.productservice.dto.ProductRequest;
import org.example.productservice.model.enums.Category;
import org.example.productservice.service.impl.DefaultProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DefaultProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateProductAndReturnCreatedStatus() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("test")
                .description("test-desc")
                .price(BigDecimal.TEN)
                .category(Category.SMARTPHONES)
                .amount(5)
                .build();

        mvc.perform(post("/api/products/")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("test-desc"))
                .andExpect(jsonPath("$.price").value(BigDecimal.TEN))
                .andExpect(jsonPath("$.category").value(Category.SMARTPHONES.name()))
                .andExpect(jsonPath("$.amount").value(5));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() throws Exception {
        long nonExistenceId = 99999L;
        mvc.perform(get("/api/products/" + nonExistenceId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPageOfProducts() throws Exception {
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

        mvc.perform(get("/api/products/")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0));
    }



}
