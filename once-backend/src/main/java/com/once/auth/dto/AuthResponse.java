/**
 * File: AuthResponse.java
 * Description:
 *  - 로그인/토큰 재발급 시 반환되는 응답 DTO
 *  - accessToken / refreshToken / message 전달
 */

package com.once.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {

    // 액세스 토큰
    private String accessToken;

    // 리프레시 토큰
    private String refreshToken;

    // 응답 메시지
    private String message;

    // 필드 설정용 생성자
    public AuthResponse(String accessToken, String refreshToken, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
    }
}