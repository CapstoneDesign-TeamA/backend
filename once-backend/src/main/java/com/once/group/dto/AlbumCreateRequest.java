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