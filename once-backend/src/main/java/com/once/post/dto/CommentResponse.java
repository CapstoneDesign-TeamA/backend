/**
 * File: CommentResponse.java
 * Description:
 *  - 댓글 조회 응답 DTO
 *  - 댓글 기본 정보와 작성자 닉네임을 포함해 반환함
 */

package com.once.post.dto;

import com.once.post.domain.PostComment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private Long groupId;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String nickname;

    // PostComment 엔티티와 사용자 닉네임을 기반으로 DTO 생성
    public static CommentResponse from(PostComment c, String nickname) {
        CommentResponse res = new CommentResponse();
        res.setId(c.getId());
        res.setPostId(c.getPostId());
        res.setUserId(c.getUserId());
        res.setNickname(nickname);
        res.setGroupId(c.getGroupId());
        res.setContent(c.getContent());
        res.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        res.setUpdatedAt(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null);
        return res;
    }
}