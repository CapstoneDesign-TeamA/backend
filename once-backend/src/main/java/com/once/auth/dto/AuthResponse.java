package com.once.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    // Getters and Setters
    private String accessToken;
    private String refreshToken;
    private String message;

    public AuthResponse(String accessToken, String refreshToken, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
    }

}