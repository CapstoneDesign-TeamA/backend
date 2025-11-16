// src/main/java/com/once/auth/controller/AuthController.java
package com.once.auth.controller;

import com.once.auth.domain.Token;
import com.once.auth.dto.AuthResponse;
import com.once.auth.dto.LoginRequest;
import com.once.auth.service.AuthService;
import com.once.user.domain.User;
import com.once.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // 로그인한 사용자 정보 임시 저장 (활동 로그용)
    public User userLog;

    /**
     * 로그인
     * 요청: { "email": "...", "password": "..." }
     * 응답: { "access_token": "...", "refresh_token": "...", "message": "로그인 성공" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authService.authenticate(loginRequest);

        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        String accessToken = authService.generateAccessToken(user);
        String refreshToken = authService.generateRefreshToken(user);
        userLog = user;

        // 활동 로그 기록
        userService.logUserActivity(user.getId(), "/auth/login", "로그인");

        AuthResponse response = new AuthResponse(accessToken, refreshToken, "로그인 성공");
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     * 요청: { "refreshToken": "..." }
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        authService.logout(refreshToken);

        // userLog 가 null일 수도 있으니 방어 코드
        if (userLog != null) {
            userService.logUserActivity(userLog.getId(), "/auth/logout", "로그아웃");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 완료");
        return ResponseEntity.ok(response);
    }

    /**
     * 액세스 토큰 재발급
     * 요청: { "refreshToken": "..." }
     * 응답: { "access_token": "...", "refresh_token": "...", "message": "토큰 재발급 성공" }
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // 1) 리프레시 토큰 유효성 검사
        if (!authService.validateRefreshToken(refreshToken)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "유효하지 않은 refresh token");
            return ResponseEntity.badRequest().body(response);
        }

        // 2) 새 액세스 토큰 발급
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        if (newAccessToken == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "토큰 재발급 실패");
            return ResponseEntity.badRequest().body(response);
        }

        // 활동 로그 기록 (userLog 가 있을 때만)
        if (userLog != null) {
            userService.logUserActivity(userLog.getId(), "/auth/refresh", "토큰 재발급");
        }

        AuthResponse response = new AuthResponse(newAccessToken, refreshToken, "토큰 재발급 성공");
        return ResponseEntity.ok(response);
    }

    /**
     * ME (테스트용)
     * 현재는 email/password 로 다시 인증해서,
     * DB에 저장된 access/refresh 토큰을 찾아서 돌려주는 구조
     *
     * 요청: { "email": "...", "password": "..." }
     * 응답: { "accessToken": "...", "refreshToken": "..." }
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authService.authenticate(loginRequest);

        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Long userId = authService.findByEmail(loginRequest.getEmail());
        Token token = authService.findTokenByUserId(userId);

        if (token == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "저장된 토큰이 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token.getAccessToken());
        response.put("refreshToken", token.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}