/**
 * File: PostCreateRequest.java
 * Description:
 *  - 게시글 생성 요청 DTO
 *  - 텍스트 내용, 이미지 URL 목록, 게시글 타입(GENERAL/MEETING_REVIEW), 모임 ID 포함
 */

package com.once.post.dto;

import com.once.post.domain.PostType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {

    private String content;          // 게시글 내용
    private List<String> imageUrls;  // 업로드된 이미지 URL 리스트
    private PostType type;           // GENERAL 또는 MEETING_REVIEW
    private Long meetingId;          // 후기인 경우 필수값
}