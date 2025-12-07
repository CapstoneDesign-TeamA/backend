/**
 * File: JwtAuthenticationFilter.java
 * Description:
 *  - JWT 인증 필터
 *  - 필요 URL 제외 후 JWT 검증 및 SecurityContext 인증 처리
 */

package com.once.config;

import com.once.auth.service.AuthService;
import com.once.auth.domain.CustomUserDetails;
import com.once.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;

    // 인증이 필요하지 않은 URL 판단
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.startsWith("/auth")) return true;
        if (path.equals("/error")) return true;

        if (request.getMethod().equals("GET") && path.matches("^/groups/\\d+$")) {
            return true;
        }

        return false;
    }

    // JWT 검증 및 SecurityContext 인증 설정
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (authService.validateToken(token)) {

                String username = authService.getUsernameFromToken(token);
                log.info(">>> JWT username = {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info(">>> userDetails id = {}", ((CustomUserDetails) userDetails).getId());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info(">>> Authentication set!");
            }
        }

        filterChain.doFilter(request, response);
    }
}