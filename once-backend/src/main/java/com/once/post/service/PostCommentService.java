/**
 * File: PostCommentService.java
 * Description:
 *  - 게시글 댓글 생성, 조회, 수정, 삭제를 처리하는 서비스
 *  - 그룹 검증, 작성자 권한 확인, 사용자 닉네임 연결 등을 포함하여 안전하게 댓글 로직을 수행
 *  - 모든 메서드는 일관된 예외 메시지와 검증 흐름을 유지함
 */

package com.once.post.service;

import com.once.post.domain.Post;
import com.once.post.domain.PostComment;
import com.once.post.dto.CommentCreateRequest;
import com.once.post.dto.CommentResponse;
import com.once.post.repository.PostCommentRepository;
import com.once.post.repository.PostRepository;
import com.once.user.domain.User;
import com.once.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    @Transactional
    public CommentResponse createComment(Long groupId, Long postId, Long userId, CommentCreateRequest req) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 그룹 검증
        if (!post.getGroupId().equals(groupId)) {
            throw new RuntimeException("해당 그룹의 게시글이 아닙니다.");
        }

        PostComment comment = PostComment.builder()
                .postId(postId)
                .userId(userId)
                .groupId(groupId)
                .content(req.getContent())
                .build();

        PostComment saved = commentRepository.save(comment);

        // 작성자 닉네임 조회
        String nickname = userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("사용자");

        return CommentResponse.from(saved, nickname);
    }

    // 댓글 목록 조회
    public List<CommentResponse> getComments(Long postId) {

        List<PostComment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(c -> {
                    String nickname = userRepository.findById(c.getUserId())
                            .map(User::getNickname)
                            .orElse("사용자");

                    return CommentResponse.from(c, nickname);
                })
                .toList();
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long groupId, Long commentId, Long userId, CommentCreateRequest req) {

        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 그룹 검증
        if (!comment.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보가 일치하지 않습니다.");
        }

        // 작성자 검증
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        comment.setContent(req.getContent());

        String nickname = userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("사용자");

        return CommentResponse.from(comment, nickname);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long groupId, Long commentId, Long userId) {

        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 그룹 검증
        if (!comment.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보가 일치하지 않습니다.");
        }

        // 작성자 검증
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}