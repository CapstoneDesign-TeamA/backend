package com.once.ai.controller;

import com.once.ai.dto.AiCategoryRequest;
import com.once.ai.dto.AiImageUrlRequest;
import com.once.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // 1) 활동 이미지 분석 (URL)
    @PostMapping("/classify/url")
    public ResponseEntity<?> classifyUrl(@RequestBody AiImageUrlRequest req) {
        Map<String, Object> body = Map.of("image_url", req.getImage_url());
        return ResponseEntity.ok(aiService.analyzeImageUrl(body));
    }

    // 2) 활동 이미지 분석 (파일)
    @PostMapping(value = "/classify/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> classifyUpload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(aiService.analyzeImageFile(file));
    }

    // 3) 그룹 활동 분석
    @PostMapping("/analysis")
    public ResponseEntity<?> analysis(@RequestBody AiCategoryRequest req) {
        Map<String, Object> body = Map.of(
                "user_categories", req.getUser_categories(),
                "group_categories", req.getGroup_categories()
        );
        return ResponseEntity.ok(aiService.analyzeCategories(body));
    }

    // 4) 다음 활동 추천
    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestBody AiCategoryRequest req) {
        Map<String, Object> body = Map.of(
                "user_categories", req.getUser_categories(),
                "group_categories", req.getGroup_categories()
        );
        return ResponseEntity.ok(aiService.recommend(body));
    }
}