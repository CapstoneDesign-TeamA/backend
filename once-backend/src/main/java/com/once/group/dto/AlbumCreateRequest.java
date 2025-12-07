/**
 * File: AlbumCreateRequest.java
 * Description:
 *  - 앨범 생성 요청 DTO
 *  - title, description 입력
 *  - imageUrl: 업로드된 이미지 URL
 */

package com.once.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumCreateRequest {

    private String title;        // 앨범 제목

    private String description;  // 앨범 설명

    @JsonProperty("imageUrl")
    private String imageUrl;     // 대표 이미지 URL
}