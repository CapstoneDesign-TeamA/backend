/**
 * Service: AiService.java
 * Description:
 *  - 이미지 URL 분석 요청
 *  - 이미지 파일 분석 요청
 *  - 활동 카테고리 분석
 *  - 사용자/그룹 기반 추천
 *  - 게시글 종합 분석 (이미지 + 텍스트)
 */

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
                "user_categories", categories,
                "group_categories", categories
        );

        return aiWebClient.post()
                .uri("/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // 5) 게시글 종합 분석 (이미지 URL + 텍스트 내용)
    public Map analyzePost(List<String> imageUrls, String content) {
        Map<String, Object> body = Map.of(
                "image_urls", imageUrls,
                "content", content != null ? content : ""
        );

        return aiWebClient.post()
                .uri("/analyze/post")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}