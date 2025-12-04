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

    @PostMapping("/classify/url")
    public ResponseEntity<?> classifyUrl(@RequestBody AiImageUrlRequest req) {
        Map<String, Object> body = Map.of("image_url", req.getImage_url());
        return ResponseEntity.ok(aiService.analyzeImageUrl(body));
    }

    @PostMapping(value = "/classify/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> classifyUpload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(aiService.analyzeImageFile(file));
    }

    @PostMapping("/analysis")
    public ResponseEntity<?> analysis(@RequestBody AiCategoryRequest req) {
        Map<String, Object> body = Map.of(
                "user_categories", req.getUser_categories(),
                "group_categories", req.getGroup_categories()
        );
        return ResponseEntity.ok(aiService.analyzeCategories(body));
    }

    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestBody AiCategoryRequest req) {
        Map<String, Object> body = Map.of(
                "user_categories", req.getUser_categories(),
                "group_categories", req.getGroup_categories()
        );
        return ResponseEntity.ok(aiService.recommend(body));
    }

    // 신규 추가: groupId 기반 추천
    @PostMapping("/recommend/group/{groupId}")
    public ResponseEntity<?> recommendByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(aiService.recommendForGroup(groupId));
    }
}