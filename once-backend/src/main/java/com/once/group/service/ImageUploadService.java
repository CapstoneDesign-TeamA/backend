package com.once.common.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageUploadService {

    private static final String PAR_BASE_URL =
            "https://objectstorage.ca-toronto-1.oraclecloud.com/p/w0f0KY74maKWN4fGv7beOkr_9RSBHwAcwJ52BfM37lR6d7H5w2S2edoAVrZWBCO9/n/yzhu49nqu7rk/b/once-bucket/o/";

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uploadUrl = PAR_BASE_URL + file.getOriginalFilename();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return uploadUrl;
        } else {
            throw new IOException("이미지 업로드 실패: " + response.getStatusCode());
        }
    }
}
