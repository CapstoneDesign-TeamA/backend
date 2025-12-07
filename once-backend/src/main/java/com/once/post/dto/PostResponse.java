/**
 * File: PostResponse.java
 * Description:
 *  - 게시글 단건 응답 DTO
 *  - 게시글 정보, 작성자 정보, 이미지 목록, 통계 정보(좋아요/댓글), AI 분석 결과 포함
 */

package com.once.post.dto;

import com.once.post.domain.Post;
import com.once.post.domain.PostImage;
import com.once.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponse {

    private Long id;
    private Long groupId;
    private Long userId;

    private String nickname;

    private String type;
    private String content;
    private String createdAt;

    private Long meetingId;

    private List<String> images;

    private int likeCount;
    private boolean myLiked;
    private int commentCount;

    private String aiKeywords;
    private String aiSentiment;
    private String aiSummary;

    private List<String> aiCategories;

    public static PostResponse from(
            Post post,
            User user,
            List<PostImage> images,
            int likeCount,
            boolean myLiked,
            int commentCount
    ) {

        PostResponse res = new PostResponse();

        // 기본 게시글 정보
        res.setId(post.getId());
        res.setGroupId(post.getGroupId());
        res.setUserId(post.getUserId());

        // 작성자 정보
        res.setNickname(user.getNickname());

        // 게시글 본문
        res.setType(post.getType().name());
        res.setContent(post.getContent());
        res.setCreatedAt(post.getCreatedAt().toString());
        res.setMeetingId(post.getMeetingId());

        // AI 분석 요소
        res.setAiKeywords(post.getAiKeywords());
        res.setAiSentiment(post.getAiSentiment());
        res.setAiSummary(post.getAiSummary());

        // 이미지 URL 리스트
        res.setImages(
                images.stream()
                        .map(PostImage::getImageUrl)
                        .toList()
        );

        // 이미지별 AI 카테고리 리스트
        res.setAiCategories(
                images.stream()
                        .map(PostImage::getAiCategory)
                        .toList()
        );

        // 좋아요/댓글 정보
        res.setLikeCount(likeCount);
        res.setMyLiked(myLiked);
        res.setCommentCount(commentCount);

        return res;
    }
}