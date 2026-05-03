package com.example.grocerystore.dto.response;

import com.example.grocerystore.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartResponse {
    private List<CartItemDto> items;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal reduction;
    private BigDecimal finalTotal;
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CartItemDto {
        private String id;
        private ProductDto product;
        private Integer quantity;
        private BigDecimal subtotal;
        
        public static CartItemDto fromEntity(CartItem item) {
            return CartItemDto.builder()
                .id(item.getId())
                .product(ProductDto.fromEntity(item.getProduct()))
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
        }
    }
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProductDto {
        private String id;
        private String name;
        private String nameEn;
        private String nameRu;
        private String description;
        private String descriptionEn;
        private String descriptionRu;
        private BigDecimal price;
        private String imageUrl;
        private String category;
        private String categoryEn;
        private String categoryRu;
        private Integer soldCount;
        private Integer stock;
        
        public static ProductDto fromEntity(com.example.grocerystore.entity.Product product) {
            return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .nameEn(product.getNameEn())
                .nameRu(product.getNameRu())
                .description(product.getDescription())
                .descriptionEn(product.getDescriptionEn())
                .descriptionRu(product.getDescriptionRu())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory() != null ? product.getCategory().getName() : null)
                .categoryEn(product.getCategory() != null ? product.getCategory().getNameEn() : null)
                .categoryRu(product.getCategory() != null ? product.getCategory().getNameRu() : null)
                .soldCount(product.getSoldCount())
                .stock(product.getStock())
                .build();
        }
    }
    
    public static CartResponse fromEntities(List<CartItem> items) {
        BigDecimal original = items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal discount = original.compareTo(BigDecimal.valueOf(100)) > 0 
            ? original.multiply(BigDecimal.valueOf(0.1)) : BigDecimal.ZERO;
        BigDecimal reduction = original.compareTo(BigDecimal.valueOf(200)) > 0 
            ? BigDecimal.valueOf(20) : BigDecimal.ZERO;
        BigDecimal finalTotal = original.subtract(discount).subtract(reduction);
        
        return CartResponse.builder()
            .items(items.stream().map(CartItemDto::fromEntity).collect(Collectors.toList()))
            .originalPrice(original)
            .discount(discount)
            .reduction(reduction)
            .finalTotal(finalTotal)
            .build();
    }
}
