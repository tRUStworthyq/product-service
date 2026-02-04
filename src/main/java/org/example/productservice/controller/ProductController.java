package org.example.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.productservice.controller.advice.GlobalExceptionHandler;
import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.example.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product catalog", description = "API for catalog management")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
        )
})
public class ProductController {

    private final ProductService productService;


    @GetMapping(value = "/")
    @Operation(summary = "Find page of products")
    @ApiResponse(
            responseCode = "200",
            description = "Product page found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
            )
    )
    public ResponseEntity<Page<ProductResponse>> findPageOfProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.findPageOfProducts(pageable));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Find product by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new product")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }
}
