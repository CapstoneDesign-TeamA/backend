package com.once.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 공개 엔드포인트
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/signup").permitAll()
                        .requestMatchers("/users/check-email").permitAll()
                        .requestMatchers("/users/check-username").permitAll()
                        .requestMatchers("/users/check-nickname").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/calendar").permitAll()

                        // ★ GET /groups/** 는 완전 공개
                        .requestMatchers(HttpMethod.GET, "/groups/**").permitAll()

                        // ★ POST/PUT/DELETE /groups/** 인증 필요
                        .requestMatchers(HttpMethod.POST, "/groups/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/groups/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/groups/**").authenticated()

                        // 유저 정보 API 인증 필요
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/profile").authenticated()
                        .requestMatchers("/users/interests").authenticated()
                        .requestMatchers("/users/terms").authenticated()
                        .requestMatchers("/users/activity-logs").authenticated()
                        .requestMatchers("/events/**").authenticated()

                        // 그 외 요청은 모두 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}