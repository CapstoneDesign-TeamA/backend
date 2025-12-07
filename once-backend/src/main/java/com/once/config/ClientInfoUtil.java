/**
 * File: ClientInfoUtil.java
 * Description:
 *  - HTTP 요청 기반 클라이언트 정보 조회 유틸
 *  - 클라이언트 IP, User-Agent 조회 기능 제공
 */

package com.once.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ClientInfoUtil {

    // 클라이언트 IP 조회
    public static String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 여러 IP가 있을 경우 첫 번째 반환
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    // User-Agent 조회
    public static String getClientUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";

        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }

    // 현재 요청 객체 조회
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}