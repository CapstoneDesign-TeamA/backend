package com.once.group.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {

    private static final String PAR_BASE_URL =
            "https://objectstorage.ca-toronto-1.oraclecloud.com/p/w0f0KY74maKWN4fGv7beOkr_9RSBHwAcwJ52BfM37lR6d7H5w2S2edoAVrZWBCO9/n/yzhu49nqu7rk/b/once-bucket/o/";

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 파일명 안전하게 변환 (UUID + 원본 확장자 유지)
        String original = file.getOriginalFilename();
        String ext = "";

        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String safeFileName = UUID.randomUUID() + ext;

        // 2. URL 인코딩
        String encodedFileName = URLEncoder.encode(safeFileName, StandardCharsets.UTF_8);

        String uploadUrl = PAR_BASE_URL + encodedFileName;

        // 3. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // 4. OCI 업로드 (PUT)
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // 최종 접근 가능한 URL 반환
            return uploadUrl;
        } else {
            throw new IOException("이미지 업로드 실패: " + response.getStatusCode());
        }
    }

    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();

        if (files == null) {
            return urls;
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                urls.add(uploadImage(file));
            }
        }
        return urls;
    }

}