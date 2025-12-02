package com.once.auth.util;

import com.once.auth.domain.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User user) {

        }

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        return null;
    }
}