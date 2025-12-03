package com.once.post.dto;

import com.once.post.domain.PostType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PostCreateRequest {

    private String content;         // 게시물 내용
    private List<String> imageUrls; // 업로드된 사진 URL 리스트

    private PostType type;          // GENERAL or MEETING_REVIEW
    private Long meetingId;         // 후기라면 필수
}