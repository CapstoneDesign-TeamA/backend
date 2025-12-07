/**
 * File: PostUpdateRequest.java
 * Description:
 *  - 게시글 수정 요청 DTO
 *  - 텍스트 내용 변경, 기존 이미지 유지 목록, 신규 이미지 URL 목록을 포함
 */

package com.once.post.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostUpdateRequest {

    private String content;           // 수정된 게시글 내용

    private List<Long> keepImageIds;  // 유지할 기존 이미지 ID 목록

    private List<String> newImages;   // 새로 추가할 이미지 URL 목록
}