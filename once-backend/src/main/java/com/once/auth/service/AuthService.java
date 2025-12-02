package com.once.auth.service;

import com.once.auth.domain.Token;
import com.once.auth.dto.LoginRequest;
import com.once.auth.mapper.AuthMapper;
import com.once.user.domain.User;
import com.once.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final String jwtSecret;
    private final long jwtExpiration;

    private final UserService userService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserService userService,
            AuthMapper authMapper,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.userService = userService;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 로그인 시 인증
    public User authenticate(LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return user;
        }
        return null;
    }

    // 액세스 토큰 생성
    public String generateAccessToken(User user) {
        SecretKey key = getSigningKey();

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(user.getEmail())          // 이메일을 subject로 사용
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 userId 추출
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey key = getSigningKey();

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("userId", Long.class);

        } catch (Exception e) {
            logger.error("JWT parsing error: {}", e.getMessage());
            return null;
        }
    }

    // 리프레시 토큰 생성 + DB 저장
    public String generateRefreshToken(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();

        Token token = new Token();
        token.setUserId(user.getId());
        token.setRefreshToken(refreshToken);
        token.setAccessToken(accessToken);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        authMapper.insertToken(token);
        return refreshToken;
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 이메일(subject) 추출
    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = getSigningKey();

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            logger.debug("Parsed subject(email) from JWT = {}", subject);
            return subject;
        } catch (Exception e) {
            logger.error("JWT parsing error: {}", e.getMessage());
            return null;
        }
    }

    // 리프레시 토큰 유효성 검증
    public boolean validateRefreshToken(String refreshToken) {
        Token token = authMapper.findByRefreshToken(refreshToken).orElse(null);
        if (token == null) return false;
        if (token.getExpiryDate() == null) return false;
        return token.getExpiryDate().isAfter(LocalDateTime.now());
    }

    // 로그아웃: 리프레시 토큰 삭제
    public void logout(@NotBlank String refreshToken) {
        authMapper.deleteToken(refreshToken);
    }

    // 리프레시 토큰으로 새 액세스 토큰 발급
    public String refreshAccessToken(String refreshToken) {
        Token token = authMapper.findByRefreshToken(refreshToken).orElse(null);
        if (token == null || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null;
        }

        User user = userService.findById(token.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }

        String newAccessToken = generateAccessToken(user);

        Token newToken = new Token();
        newToken.setRefreshToken(refreshToken);
        newToken.setAccessToken(newAccessToken);
        newToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        authMapper.updateToken(newToken);

        return newAccessToken;
    }

    public Token findTokenByUserId(Long id) {
        return authMapper.findTokenByUserId(id).orElse(null);
    }

    public long findByEmail(String email) {
        return authMapper.findByEmail(email);
    }
}