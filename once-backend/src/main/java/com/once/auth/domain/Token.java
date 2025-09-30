package com.once.auth.domain;



import java.time.LocalDateTime;


public class Token {
    // Getters and Setters
    private Long id;
    private Long user_id;
    private String refresh_token;
    private String access_token;
    private LocalDateTime expiry_date;
    private LocalDateTime created_at;

    // 默认构造函数
    public Token() {}

    // 带参数的构造函数
    public Token(Long userId, String refreshToken, String accessToken, LocalDateTime expiryDate) {
        this.user_id = userId;
        this.refresh_token = refreshToken;
        this.expiry_date = expiryDate;
        this.access_token = accessToken;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long userId) {
        this.user_id = userId;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public LocalDateTime getExpiryDate() {
        return expiry_date;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiry_date = expiryDate;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

}