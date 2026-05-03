package com.example.grocerystore.service;

import com.example.grocerystore.dto.request.CreateOrderRequest;
import com.example.grocerystore.dto.response.CartResponse;
import com.example.grocerystore.dto.response.OrderResponse;
import com.example.grocerystore.dto.response.PageResponse;
import com.example.grocerystore.entity.*;
import com.example.grocerystore.exception.ResourceNotFoundException;
import com.example.grocerystore.repository.CartItemRepository;
import com.example.grocerystore.repository.OrderRepository;
import com.example.grocerystore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductRepository productRepository;

    public PageResponse<OrderResponse> getOrders(String email, int page, int size) {
        User user = userService.getUserByEmail(email);
        Page<Order> orderPage = orderRepository.findByUserIdOrderByOrderDateDesc(
                user.getId(), PageRequest.of(page, size, Sort.by("orderDate").descending()));

        return PageResponse.<OrderResponse>builder()
                .content(orderPage.getContent().stream()
                        .map(OrderResponse::fromEntity)
                        .collect(Collectors.toList()))
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    public OrderResponse getOrderById(String email, String orderId) {
        User user = userService.getUserByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order does not belong to user");
        }

        return OrderResponse.fromEntity(order);
    }

    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        User user = userService.getUserByEmail(email);
        
        CartResponse cart = cartService.getCart(email);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        String orderNumber = "ORD" + System.currentTimeMillis();
        
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber(orderNumber)
                .user(user)
                .originalPrice(cart.getOriginalPrice())
                .discount(cart.getDiscount())
                .reduction(cart.getReduction())
                .finalTotal(cart.getFinalTotal())
                .shippingAddress(request.getShippingAddress())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .status(OrderStatus.PENDING)
                .build();

        for (CartResponse.CartItemDto cartItem : cart.getItems()) {
            com.example.grocerystore.entity.Product product = 
                    productRepository.findById(cartItem.getProduct().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + cartItem.getProduct().getId()));
            
            OrderItem orderItem = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .order(order)
                    .product(product)
                    .unitPrice(cartItem.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(cartItem.getSubtotal())
                    .build();
            
            order.addItem(orderItem);
        }

        orderRepository.save(order);
        cartService.clearCart(email);

        return OrderResponse.fromEntity(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        return OrderResponse.fromEntity(order);
    }
}
