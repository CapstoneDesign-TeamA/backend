package com.once.ai.service;

import com.once.group.repository.GroupActivityCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient aiWebClient;
    private final GroupActivityCategoryRepository groupActivityCategoryRepository;

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
                .bodyValue(file)
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

    // 4) 직접 카테고리 받아서 추천
    public Map recommend(Map<String, Object> body) {
        return aiWebClient.post()
                .uri("/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // 4) 그룹 기반 활동 추천
    public Map recommendForGroup(Long groupId) {

        List<String> categories = groupActivityCategoryRepository.findRecentCategories(groupId);

        Map<String, Object> body = Map.of(
                "user_categories", categories,   // 필요 시 구분 가능
                "group_categories", categories
        );

        return aiWebClient.post()
                .uri("/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}