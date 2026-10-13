package com.xadice.gearflow.controller;

import com.xadice.gearflow.dto.response.AdminUserResponse;
import com.xadice.gearflow.dto.response.PageResponse;

import com.xadice.gearflow.service.AdminUserService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService
    ) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<AdminUserResponse>>
    getAllUsers(

            @PageableDefault(
                    size = 10,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                adminUserService.getAllUsers(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminUserService.getUserById(id)
        );
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<AdminUserResponse> disableUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminUserService.disableUser(id)
        );
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<AdminUserResponse> enableUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminUserService.enableUser(id)
        );
    }
}