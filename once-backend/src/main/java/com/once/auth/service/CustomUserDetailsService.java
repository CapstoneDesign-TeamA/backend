/**
 * File: CustomUserDetailsService.java
 * Description:
 *  - Spring Security용 사용자 정보 조회 서비스
 *  - 이메일 또는 userId 기반 UserDetails 반환
 */

package com.once.auth.service;

import com.once.auth.domain.CustomUserDetails;
import com.once.user.domain.User;
import com.once.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일 기반 사용자 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new CustomUserDetails(user);
    }

    // userId 기반 사용자 조회
    public CustomUserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword()
        );
    }
}