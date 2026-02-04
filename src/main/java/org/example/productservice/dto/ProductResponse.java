package org.example.productservice.dto;


import lombok.Builder;
import org.example.productservice.model.enums.Category;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Category category,
        Integer amount
) {
}
