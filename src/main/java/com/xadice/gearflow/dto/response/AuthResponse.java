package com.xadice.gearflow.dto.response;

import com.xadice.gearflow.model.Role;

public class AuthResponse {

    private String token;
    private String tokenType;

    private Long userId;
    private String fullName;
    private String email;
    private Role role;

    public AuthResponse() {
    }

    public AuthResponse(
            String token,
            String tokenType,
            Long userId,
            String fullName,
            String email,
            Role role
    ) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}