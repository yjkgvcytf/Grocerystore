package com.example.grocerystore.repository;

import com.example.grocerystore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    
    List<CartItem> findByUserId(String userId);
    
    Optional<CartItem> findByUserIdAndProductId(String userId, String productId);
    
    void deleteByUserId(String userId);
    
    @Query("SELECT SUM(c.product.price * c.quantity) FROM CartItem c WHERE c.user.id = :userId")
    Double getCartTotalByUserId(String userId);
}
