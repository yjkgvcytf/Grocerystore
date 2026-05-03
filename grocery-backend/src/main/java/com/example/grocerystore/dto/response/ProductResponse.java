package com.example.grocerystore.dto.response;

import com.example.grocerystore.entity.Product;
import com.example.grocerystore.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String nameEn;
    private String nameRu;
    private String description;
    private String descriptionEn;
    private String descriptionRu;
    private BigDecimal price;
    private String imageUrl;
    private String categoryId;
    private String category;
    private String categoryEn;
    private String categoryRu;
    private Integer soldCount;
    private Integer stock;
    private Boolean featured;
    
    public static ProductResponse fromEntity(Product product) {
        Category cat = product.getCategory();
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .nameEn(product.getNameEn())
            .nameRu(product.getNameRu())
            .description(product.getDescription())
            .descriptionEn(product.getDescriptionEn())
            .descriptionRu(product.getDescriptionRu())
            .price(product.getPrice())
            .imageUrl(product.getImageUrl())
            .categoryId(cat != null ? cat.getId() : null)
            .category(cat != null ? cat.getName() : null)
            .categoryEn(cat != null ? cat.getNameEn() : null)
            .categoryRu(cat != null ? cat.getNameRu() : null)
            .soldCount(product.getSoldCount())
            .stock(product.getStock())
            .featured(product.getFeatured())
            .build();
    }
}
