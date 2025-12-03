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

        res.setId(post.getId());
        res.setGroupId(post.getGroupId());
        res.setUserId(post.getUserId());
        res.setNickname(user.getNickname());

        res.setType(post.getType().name());
        res.setContent(post.getContent());
        res.setCreatedAt(post.getCreatedAt().toString());
        res.setMeetingId(post.getMeetingId());

        res.setAiKeywords(post.getAiKeywords());
        res.setAiSentiment(post.getAiSentiment());
        res.setAiSummary(post.getAiSummary());

        // URL 리스트만 프론트에 전달 → 사진 안 깨짐
        res.setImages(images.stream().map(PostImage::getImageUrl).toList());

        // AI 카테고리 리스트는 별도 전달
        res.setAiCategories(images.stream()
                .map(PostImage::getAiCategory)
                .toList()
        );

        res.setLikeCount(likeCount);
        res.setMyLiked(myLiked);
        res.setCommentCount(commentCount);

        return res;
    }
}