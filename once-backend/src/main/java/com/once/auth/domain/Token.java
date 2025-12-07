/**
 * File: Token.java
 * Description:
 *  - 사용자 토큰 저장 도메인
 *  - access/refresh 토큰 및 만료일 관리
 */

package com.once.auth.domain;

import java.time.LocalDateTime;

public class Token {

    // 토큰 PK
    private Long id;

    // 사용자 ID
    private Long user_id;

    // 리프레시 토큰
    private String refresh_token;

    // 액세스 토큰
    private String access_token;

    // 토큰 만료일
    private LocalDateTime expiry_date;

    // 생성일
    private LocalDateTime created_at;

    // 기본 생성자
    public Token() {}

    // 토큰 생성자 (userId + 토큰 정보)
    public Token(Long userId, String refreshToken, String accessToken, LocalDateTime expiryDate) {
        this.user_id = userId;
        this.refresh_token = refreshToken;
        this.expiry_date = expiryDate;
        this.access_token = accessToken;
    }

    // getter/setter
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