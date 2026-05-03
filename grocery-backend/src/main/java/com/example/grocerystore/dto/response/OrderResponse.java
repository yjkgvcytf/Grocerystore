package com.example.grocerystore.dto.response;

import com.example.grocerystore.entity.Order;
import com.example.grocerystore.entity.OrderItem;
import com.example.grocerystore.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {
    private String id;
    private String orderNumber;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal reduction;
    private BigDecimal finalTotal;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItemDto> items;
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrderItemDto {
        private String id;
        private String productId;
        private String productName;
        private String productNameEn;
        private String productNameRu;
        private String imageUrl;
        private String category;
        private String categoryEn;
        private String categoryRu;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        
        public static OrderItemDto fromEntity(OrderItem item) {
            com.example.grocerystore.entity.Product product = item.getProduct();
            com.example.grocerystore.entity.Category category = product != null ? product.getCategory() : null;
            
            return OrderItemDto.builder()
                .id(item.getId())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .productNameEn(product != null ? product.getNameEn() : null)
                .productNameRu(product != null ? product.getNameRu() : null)
                .imageUrl(product != null ? product.getImageUrl() : null)
                .category(category != null ? category.getName() : null)
                .categoryEn(category != null ? category.getNameEn() : null)
                .categoryRu(category != null ? category.getNameRu() : null)
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
        }
    }
    
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .originalPrice(order.getOriginalPrice())
            .discount(order.getDiscount())
            .reduction(order.getReduction())
            .finalTotal(order.getFinalTotal())
            .shippingAddress(order.getShippingAddress())
            .recipientName(order.getRecipientName())
            .recipientPhone(order.getRecipientPhone())
            .status(order.getStatus())
            .orderDate(order.getOrderDate())
            .items(order.getItems().stream().map(OrderItemDto::fromEntity).collect(Collectors.toList()))
            .build();
    }
}
