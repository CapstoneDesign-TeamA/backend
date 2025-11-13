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
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // 로그에 표시할 헤더 (나머지는 모두 무시)
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "authorization",
            "content-type",
            "user-agent"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

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

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n==================== [HTTP REQUEST] ====================\n");

        sb.append(request.getMethod()).append(" ").append(request.getRequestURI());
        if (request.getQueryString() != null)
            sb.append("?").append(request.getQueryString());
        sb.append("\n");

        sb.append("Headers:\n");
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            if (ALLOWED_HEADERS.contains(name.toLowerCase())) {
                sb.append("  ").append(name).append(": ").append(request.getHeader(name)).append("\n");
            }
        });

        byte[] body = request.getContentAsByteArray();
        if (body.length > 0) {
            String rawJson = new String(body, StandardCharsets.UTF_8);
            sb.append("Body:\n");
            sb.append(prettyJson(rawJson)).append("\n");
        } else {
            sb.append("Body: <empty>\n");
        }

        sb.append("=========================================================\n");

        logger.info(sb.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n==================== [HTTP RESPONSE] ====================\n");

        sb.append("Status: ").append(response.getStatus()).append("\n");

        byte[] body = response.getContentAsByteArray();
        if (body.length > 0) {
            String rawJson = new String(body, StandardCharsets.UTF_8);
            sb.append("Body:\n");
            sb.append(prettyJson(rawJson)).append("\n");
        } else {
            sb.append("Body: <empty>\n");
        }

        sb.append("==========================================================\n");

        logger.info(sb.toString());
    }

    private String prettyJson(String json) {
        try {
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json; // JSON이 아니면 raw 출력
        }
    }
}