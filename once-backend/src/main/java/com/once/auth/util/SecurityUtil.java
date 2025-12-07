/**
 * File: SecurityUtil.java
 * Description:
 *  - 현재 인증된 사용자 ID 조회 유틸리티
 *  - SecurityContextHolder에서 CustomUserDetails 추출
 */

package com.once.auth.util;

import com.once.auth.domain.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    // 현재 사용자 ID 반환
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보 없음
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // CustomUserDetails 타입이면 ID 반환
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        return null;
    }
}