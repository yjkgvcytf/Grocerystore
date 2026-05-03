package com.example.grocerystore.service;

import com.example.grocerystore.dto.request.UpdateProfileRequest;
import com.example.grocerystore.dto.response.AuthResponse;
import com.example.grocerystore.entity.User;
import com.example.grocerystore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AuthResponse.UserDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return AuthResponse.UserDto.fromEntity(user);
    }

    public AuthResponse.UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getShippingAddress() != null) {
            user.setShippingAddress(request.getShippingAddress());
        }

        userRepository.save(user);
        return AuthResponse.UserDto.fromEntity(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
