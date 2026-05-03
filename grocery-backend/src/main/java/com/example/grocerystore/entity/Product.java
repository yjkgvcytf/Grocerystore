package com.example.grocerystore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    
    @Id
    @Column(length = 36)
    private String id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(name = "name_en", length = 200)
    private String nameEn;
    
    @Column(name = "name_ru", length = 200)
    private String nameRu;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;
    
    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "sold_count")
    @Builder.Default
    private Integer soldCount = 0;
    
    @Column
    @Builder.Default
    private Integer stock = 100;
    
    @Column
    @Builder.Default
    private Boolean featured = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
