package com.mini_ecommerce.users_service.service;

import com.mini_ecommerce.users_service.dto.UserCreateRequest;
import com.mini_ecommerce.users_service.dto.UserResponse;
import com.mini_ecommerce.users_service.dto.UserUpdateRequest;
import com.mini_ecommerce.users_service.exception.ConflictException;
import com.mini_ecommerce.users_service.exception.NotFoundException;
import com.mini_ecommerce.users_service.model.User;
import com.mini_ecommerce.users_service.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return toResponse(userRepository.save(user));
    }

    public UserResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ConflictException("Email already in use.");
        }

        User user = getEntity(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        User user = getEntity(id);
        userRepository.delete(user);
    }

    private User getEntity(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
