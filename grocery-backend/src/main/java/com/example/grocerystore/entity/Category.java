package com.example.grocerystore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    
    @Id
    @Column(length = 36)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "name_en", length = 100)
    private String nameEn;
    
    @Column(name = "name_ru", length = 100)
    private String nameRu;
    
    @Column(length = 50)
    private String icon;
}
