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

    private String nickname;       // 사용자 닉네임
    // private String profileImg;  // TODO: 프로필 이미지 도입 후 활성화

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

    public static PostResponse from(
            Post post,
            User user,                     // 사용자 정보
            List<PostImage> images,
            int likeCount,
            boolean myLiked,
            int commentCount               // ★ 댓글 개수도 파라미터로 전달받음
    ) {

        PostResponse res = new PostResponse();

        res.setId(post.getId());
        res.setGroupId(post.getGroupId());
        res.setUserId(post.getUserId());

        // 사용자 정보
        res.setNickname(user.getNickname());
        // res.setProfileImg(user.getProfileImg());

        res.setType(post.getType().name());
        res.setContent(post.getContent());
        res.setCreatedAt(post.getCreatedAt().toString());
        res.setMeetingId(post.getMeetingId());

        res.setAiKeywords(post.getAiKeywords());
        res.setAiSentiment(post.getAiSentiment());
        res.setAiSummary(post.getAiSummary());

        res.setImages(images.stream().map(PostImage::getImageUrl).toList());

        res.setLikeCount(likeCount);
        res.setMyLiked(myLiked);

        // ★ 댓글 개수 설정
        res.setCommentCount(commentCount);

        return res;
    }
}