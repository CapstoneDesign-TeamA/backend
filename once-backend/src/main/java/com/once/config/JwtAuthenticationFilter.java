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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        // 인증 안 필요한 URL
        if (path.startsWith("/auth")) return true;
        if (path.equals("/error")) return true;

        // 그룹 상세 조회 GET → 공개
        if (request.getMethod().equals("GET") && path.matches("^/groups/\\d+$")) {
            return true;
        }

        return false;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            // 토큰 유효성 검사
            if (authService.validateToken(token)) {

                // 토큰에서 username(email) 추출
                String username = authService.getUsernameFromToken(token);
                log.info(">>> JWT username = {}", username);

                // DB에서 UserDetails 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info(">>> userDetails id = {}", ((CustomUserDetails) userDetails).getId());

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // SecurityContext 에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info(">>> Authentication set!");
            }
        }

        filterChain.doFilter(request, response);
    }
}