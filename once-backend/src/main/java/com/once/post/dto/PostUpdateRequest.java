package com.once.post.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostUpdateRequest {

    private String content;

    // 유지할 이미지 ID 목록
    private List<Long> keepImageIds;

    // 새로 추가할 이미지 URL 목록 (OCI 업로드된 URL)
    private List<String> newImages;
}