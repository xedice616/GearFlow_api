package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.response.AdminUserResponse;
import com.xadice.gearflow.dto.response.PageResponse;

import com.xadice.gearflow.exception.InvalidOperationException;
import com.xadice.gearflow.exception.ResourceNotFoundException;

import com.xadice.gearflow.model.Role;
import com.xadice.gearflow.model.User;

import com.xadice.gearflow.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> getAllUsers(
            Pageable pageable
    ) {

        Page<AdminUserResponse> responsePage =
                userRepository
                        .findAll(pageable)
                        .map(this::mapToResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(
            Long userId
    ) {

        return mapToResponse(
                findUser(userId)
        );
    }

    public AdminUserResponse disableUser(
            Long userId
    ) {

        User user = findUser(userId);

        if (user.getRole() == Role.ADMIN) {
            throw new InvalidOperationException(
                    "Admin accounts cannot be disabled"
            );
        }

        if (!user.isEnabled()) {
            throw new InvalidOperationException(
                    "User is already disabled"
            );
        }

        user.setEnabled(false);

        return mapToResponse(
                userRepository.save(user)
        );
    }

    public AdminUserResponse enableUser(
            Long userId
    ) {

        User user = findUser(userId);

        if (user.isEnabled()) {
            throw new InvalidOperationException(
                    "User is already enabled"
            );
        }

        user.setEnabled(true);

        return mapToResponse(
                userRepository.save(user)
        );
    }

    private User findUser(
            Long userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }

    private AdminUserResponse mapToResponse(
            User user
    ) {

        return new AdminUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}