package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.CreateUserRequest;
import com.zorvyn.finance.dto.UpdateStatusRequest;
import com.zorvyn.finance.dto.UserResponse;
import com.zorvyn.finance.exception.DuplicateResourceException;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User {} created with role {}", saved.getEmail(), saved.getRole());
        return mapToResponse(saved);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateStatus(UUID id, UpdateStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(request.getActive());
        User saved = userRepository.save(user);
        log.info("User {} status updated to active={}", saved.getEmail(), saved.isActive());
        return mapToResponse(saved);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
