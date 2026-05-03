package com.example.grocerystore.dto.response;

import com.example.grocerystore.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String nameEn;
    private String nameRu;
    private String icon;
    private List<ProductResponse> products;
    
    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .nameEn(category.getNameEn())
            .nameRu(category.getNameRu())
            .icon(category.getIcon())
            .build();
    }
    
    public static CategoryResponse fromEntityWithProducts(Category category, List<ProductResponse> products) {
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .nameEn(category.getNameEn())
            .nameRu(category.getNameRu())
            .icon(category.getIcon())
            .products(products)
            .build();
    }
}
