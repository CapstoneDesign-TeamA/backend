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
    private final UserRepository userRepository; // ★ 추가

    // ===============================
    // 댓글 작성
    // ===============================
    @Transactional
    public CommentResponse createComment(Long groupId, Long postId, Long userId, CommentCreateRequest req) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

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

        // 댓글 작성자의 닉네임
        String nickname = userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("사용자");

        return CommentResponse.from(saved, nickname);
    }

    // ===============================
    // 댓글 조회
    // ===============================
    public List<CommentResponse> getComments(Long postId) {

        List<PostComment> list = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return list.stream()
                .map(c -> {
                    String nickname = userRepository.findById(c.getUserId())
                            .map(User::getNickname)
                            .orElse("사용자");

                    return CommentResponse.from(c, nickname);
                })
                .toList();
    }

    // ===============================
    // 댓글 수정
    // ===============================
    @Transactional
    public CommentResponse updateComment(Long groupId, Long commentId, Long userId, CommentCreateRequest req) {

        PostComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!c.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보가 일치하지 않습니다.");
        }

        if (!c.getUserId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        c.setContent(req.getContent());

        String nickname = userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("사용자");

        return CommentResponse.from(c, nickname);
    }

    // ===============================
    // 댓글 삭제
    // ===============================
    @Transactional
    public void deleteComment(Long groupId, Long commentId, Long userId) {

        PostComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!c.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보가 일치하지 않습니다.");
        }

        if (!c.getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(c);
    }
}