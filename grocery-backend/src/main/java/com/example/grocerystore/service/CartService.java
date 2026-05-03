package com.example.grocerystore.service;

import com.example.grocerystore.dto.request.AddToCartRequest;
import com.example.grocerystore.dto.request.UpdateCartRequest;
import com.example.grocerystore.dto.response.CartResponse;
import com.example.grocerystore.entity.CartItem;
import com.example.grocerystore.entity.Product;
import com.example.grocerystore.entity.User;
import com.example.grocerystore.exception.ResourceNotFoundException;
import com.example.grocerystore.repository.CartItemRepository;
import com.example.grocerystore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartResponse getCart(String email) {
        User user = userService.getUserByEmail(email);
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        return CartResponse.fromEntities(items);
    }

    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest request) {
        User user = userService.getUserByEmail(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .id(UUID.randomUUID().toString())
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return getCart(email);
    }

    @Transactional
    public CartResponse updateCartItem(String email, String itemId, UpdateCartRequest request) {
        User user = userService.getUserByEmail(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return getCart(email);
    }

    @Transactional
    public CartResponse removeFromCart(String email, String itemId) {
        User user = userService.getUserByEmail(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }

        cartItemRepository.delete(item);
        return getCart(email);
    }

    @Transactional
    public void clearCart(String email) {
        User user = userService.getUserByEmail(email);
        cartItemRepository.deleteByUserId(user.getId());
    }
}
