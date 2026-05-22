package com.example.backend.dto;

import com.example.backend.entity.UserEntity;

public class AuthResponse {
    private String token;
    private UserEntity user;

    public AuthResponse() {}

    public AuthResponse(String token, UserEntity user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
