/**
 * File: RequestLoggingFilter.java
 * Description:
 *  - HTTP 요청/응답 로깅 필터
 *  - 지정된 헤더만 출력하고 Body는 JSON 포맷팅하여 기록
 */

package com.once.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final ObjectMapper mapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // 로그에 허용할 헤더 목록
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "authorization",
            "content-type",
            "user-agent"
    );

    // 요청/응답 캐싱 후 필터 실행
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(req, res);
        } finally {
            logRequest(req);
            logResponse(res);
            res.copyBodyToResponse();
        }
    }

    // 요청 로그 기록
    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n==================== [HTTP REQUEST] ====================\n");

        sb.append(request.getMethod()).append(" ").append(request.getRequestURI());
        if (request.getQueryString() != null) {
            sb.append("?").append(request.getQueryString());
        }
        sb.append("\n");

        sb.append("Headers:\n");
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            if (ALLOWED_HEADERS.contains(name.toLowerCase())) {
                sb.append("  ").append(name)
                        .append(": ").append(request.getHeader(name)).append("\n");
            }
        });

        byte[] body = request.getContentAsByteArray();
        if (body.length > 0) {
            sb.append("Body:\n");
            sb.append(prettyJson(new String(body, StandardCharsets.UTF_8))).append("\n");
        } else {
            sb.append("Body: <empty>\n");
        }

        sb.append("=========================================================\n");
        logger.info(sb.toString());
    }

    // 응답 로그 기록
    private void logResponse(ContentCachingResponseWrapper response) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n==================== [HTTP RESPONSE] ====================\n");

        sb.append("Status: ").append(response.getStatus()).append("\n");

        byte[] body = response.getContentAsByteArray();
        if (body.length > 0) {
            sb.append("Body:\n");
            sb.append(prettyJson(new String(body, StandardCharsets.UTF_8))).append("\n");
        } else {
            sb.append("Body: <empty>\n");
        }

        sb.append("==========================================================\n");
        logger.info(sb.toString());
    }

    // JSON 출력 포맷팅
    private String prettyJson(String json) {
        try {
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }
}