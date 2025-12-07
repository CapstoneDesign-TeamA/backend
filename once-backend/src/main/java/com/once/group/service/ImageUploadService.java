/**
 * File: ImageUploadService.java
 * Description:
 *  - MultipartFile을 받아 OCI ObjectStorage에 업로드하는 서비스
 *  - 파일명 안전 변환(UUID), URL 인코딩, PAR URL PUT 요청 수행
 *  - 성공 시 최종 접근 가능한 이미지 URL 반환
 */

package com.once.group.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ImageUploadService {

    private static final String PAR_BASE_URL =
            "https://objectstorage.ca-toronto-1.oraclecloud.com/p/w0f0KY74maKWN4fGv7beOkr_9RSBHwAcwJ52BfM37lR6d7H5w2S2edoAVrZWBCO9/n/yzhu49nqu7rk/b/once-bucket/o/";

    public String uploadImage(MultipartFile file) throws IOException {

        // 파일 null 또는 빈 파일인 경우 무시
        if (file == null || file.isEmpty()) {
            return null;
        }

        // UUID 기반 안전한 파일명 생성
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String safeFileName = UUID.randomUUID() + ext;

        // URL 인코딩 적용
        String encodedFileName = URLEncoder.encode(safeFileName, StandardCharsets.UTF_8);
        String uploadUrl = PAR_BASE_URL + encodedFileName;

        // 요청 헤더 설정 (Content-Type 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        // 요청 본문에 파일 바이너리 포함
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // PUT 요청으로 업로드 수행
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, String.class);

        // 업로드 성공 시 최종 파일 URL 반환
        if (response.getStatusCode().is2xxSuccessful()) {
            return uploadUrl;
        }

        throw new IOException("이미지 업로드 실패: " + response.getStatusCode());
    }
}