package com.example.grocerystore.service;

import com.example.grocerystore.dto.response.CategoryResponse;
import com.example.grocerystore.dto.response.ProductResponse;
import com.example.grocerystore.entity.Category;
import com.example.grocerystore.exception.ResourceNotFoundException;
import com.example.grocerystore.repository.CategoryRepository;
import com.example.grocerystore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        List<ProductResponse> products = productRepository.findByCategoryId(id, 
                org.springframework.data.domain.Pageable.unpaged()).getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        
        return CategoryResponse.fromEntityWithProducts(category, products);
    }
}
