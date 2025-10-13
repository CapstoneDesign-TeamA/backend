package com.once.auth.service;


import com.once.config.JwtConfig;
import com.once.auth.dto.LoginRequest;
import com.once.auth.mapper.AuthMapper;
import com.once.auth.domain.Token;
import com.once.user.domain.User;
import com.once.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    private final JwtConfig jwtConfig;

    @Autowired
    public AuthService(JwtConfig jwtConfig, UserService userService, AuthMapper authMapper, PasswordEncoder passwordEncoder) {
        this.jwtConfig = jwtConfig; // 确保在此注入
        this.userService = userService;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return user;
        }
        return null;
    }
    public boolean validateToken(String token) {
        try {
            // 1. 配置的密钥
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    //传入 token
                    .parseClaimsJws(token);


            return true;
        } catch (Exception e) {
            // 如果解析过程中出错（比如签名不对、过期、格式错误），说明 token 无效
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }


    // 从令牌中获取用户名/邮箱的方法
    public  String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("JWT parsing error: {}", e.getMessage());
            return null;
        }
    }
    public String generateAccessToken(User user) {

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // 2. 创建并返回 JWT


        return Jwts.builder()
                // 设置 token 的主体 (subject)，这里用用户的 email
                .setSubject(user.getEmail())

                // 自定义字段 (claims)，可以放用户的额外信息
                .claim("userId", user.getId())
                .claim("username", user.getUsername())

                // 签发时间 (iat)
                .setIssuedAt(new Date())

                // 过期时间 (exp)，当前时间 + jwtExpiration（你配置的毫秒数）
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))

                // 使用 HS256 算法和密钥进行签名
                .signWith(key, SignatureAlgorithm.HS256)

                // 压缩成字符串形式
                .compact();
    }

    public String generateRefreshToken(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();

        // 2. 创建一个 Token 实体对象
        Token token = new Token();
        token.setUserId(user.getId());                       // 关联到当前用户
        token.setRefreshToken(refreshToken);;
        token.setAccessToken(accessToken);// 保存生成的 refresh token
        token.setExpiryDate(LocalDateTime.now().plusDays(7)); // 设置过期时间：7天后


        authMapper.insertToken(token);

        return refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken) {
        Token token = authMapper.findByRefreshToken(refreshToken).orElse(null);
        if (token == null) {
            return false;
        }
        if (token.getExpiryDate() == null) {
            return false;
        }

        return token.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void logout(String refreshToken) {
        authMapper.deleteToken(refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        Token token = authMapper.findByRefreshToken(refreshToken).orElse(null);
        if (token == null || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null;
        }

        User user = userService.findById(token.getUserId()).orElse(null); // 这里使用 findById
        if (user == null) {
            return null;
        }
        String New_accessToken = generateAccessToken(user);
        Token New_token = new Token();
        New_token.setRefreshToken(refreshToken);
        New_token.setAccessToken(New_accessToken);// 保存生成的 refresh token
        New_token.setExpiryDate(LocalDateTime.now().plusDays(7));// 设置过期时间：7天后

        authMapper.updateToken(New_token);


        return New_accessToken;
    }


    public Token findTokenByUserId(Long id) {
        return authMapper.findTokenByUserId(id).orElse(null);

    }
//    public String findRTokenByUserId(Long id) {
//        return authMapper.findRTokenByUserId(id);
//
//    }

    public long findByEmail(String email) {
        return authMapper.findByEmail(email);
    }
}