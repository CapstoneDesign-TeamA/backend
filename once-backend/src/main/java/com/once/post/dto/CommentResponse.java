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