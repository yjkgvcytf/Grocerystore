package com.example.grocerystore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    private Integer quantity = 1;
}
