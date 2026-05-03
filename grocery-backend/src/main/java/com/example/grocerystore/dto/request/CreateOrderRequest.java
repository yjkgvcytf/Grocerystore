package com.example.grocerystore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Recipient name is required")
    private String recipientName;
    
    @NotBlank(message = "Recipient phone is required")
    private String recipientPhone;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}
