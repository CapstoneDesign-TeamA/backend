/**
 * File: CommentCreateRequest.java
 * Description:
 *  - 댓글 생성 요청 DTO
 *  - 클라이언트로부터 전달되는 댓글 내용만 포함됨
 */

package com.once.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRequest {

    private String content;  // 댓글 내용
}