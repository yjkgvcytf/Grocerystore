package com.example.grocerystore.dto.response;

import com.example.grocerystore.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;
    
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserDto {
        private String id;
        private String email;
        private String fullName;
        private String phone;
        private String shippingAddress;
        
        public static UserDto fromEntity(User user) {
            return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .shippingAddress(user.getShippingAddress())
                .build();
        }
    }
}
