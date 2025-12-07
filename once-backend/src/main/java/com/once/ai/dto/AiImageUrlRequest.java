/**
 * File: AiImageUrlRequest.java
 * Description:
 *  - 단일 이미지 URL 전달 요청 DTO
 */

package com.once.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiImageUrlRequest {
    private String image_url;
}