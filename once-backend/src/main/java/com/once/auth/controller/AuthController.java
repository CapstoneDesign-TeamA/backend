package com.once.auth.controller;

import com.once.auth.domain.Token;
import com.once.auth.dto.AuthResponse;
import com.once.auth.dto.LoginRequest;
import com.once.user.domain.User;
import com.once.auth.service.AuthService;
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


    public User userLog;

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        authService.logout(refreshToken);

        // 활동 로그 기록
        userService.logUserActivity(userLog.getId(), "/auth/logout", "로그아웃");

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 완료");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!authService.validateRefreshToken(refreshToken)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "유효하지 않은 refresh token");
            return ResponseEntity.badRequest().body(response);
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        if (newAccessToken == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "토큰 재발급 실패");
            return ResponseEntity.badRequest().body(response);
        }

        // 활동 로그 기록
        userService.logUserActivity(userLog.getId(), "/auth/refresh", "토근 재발급");

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", refreshToken);
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/me")
    public ResponseEntity<?> Me(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authService.authenticate(loginRequest);

        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        Long userid = authService.findByEmail(loginRequest.getEmail());

        Token token = authService.findTokenByUserId(userid);
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        // 활동 로그 기록
//        userService.logUserActivity(userLog.getId(), "ME", "");

        Map<String, String> response = new HashMap<>();
        response.put("accessToken",accessToken );
        response.put("refreshToken",refreshToken );
        return ResponseEntity.ok(response);
    }
}