package com.once.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient aiWebClient;

    // 1) URL 이미지 분석
    public Map analyzeImageUrl(Map<String, Object> body) {
        return aiWebClient.post()
                .uri("/classify/url")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // 2) 파일 이미지 분석
    public Map analyzeImageFile(MultipartFile file) {
        return aiWebClient.post()
                .uri("/classify/upload")
                .bodyValue(file) // Multipart 전송
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // 3) 활동 분석
    public Map analyzeCategories(Map<String, Object> body) {
        return aiWebClient.post()
                .uri("/analysis")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // 4) 활동 추천
    public Map recommend(Map<String, Object> body) {
        return aiWebClient.post()
                .uri("/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}