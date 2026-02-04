package org.example.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.example.productservice.model.enums.Category;

import java.math.BigDecimal;

@Builder
public record ProductRequest(
        @NotBlank
        @Size(max = 200)
        String name,
        @Size(max = 500)
        String description,
        @NotNull
        @Positive
        BigDecimal price,
        Category category,
        @PositiveOrZero
        Integer amount
) {
}
