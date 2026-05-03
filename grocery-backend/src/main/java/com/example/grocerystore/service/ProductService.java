package com.example.grocerystore.service;

import com.example.grocerystore.dto.response.CategoryResponse;
import com.example.grocerystore.dto.response.PageResponse;
import com.example.grocerystore.dto.response.ProductResponse;
import com.example.grocerystore.entity.Category;
import com.example.grocerystore.entity.Product;
import com.example.grocerystore.exception.ResourceNotFoundException;
import com.example.grocerystore.repository.CategoryRepository;
import com.example.grocerystore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);
        
        return PageResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream()
                        .map(ProductResponse::fromEntity)
                        .collect(Collectors.toList()))
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public PageResponse<ProductResponse> getProductsByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        
        return PageResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream()
                        .map(ProductResponse::fromEntity)
                        .collect(Collectors.toList()))
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    public List<ProductResponse> getFeaturedProducts(int limit) {
        return productRepository.findByFeaturedTrue().stream()
                .limit(limit)
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PageResponse<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
        
        return PageResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream()
                        .map(ProductResponse::fromEntity)
                        .collect(Collectors.toList()))
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }
}
