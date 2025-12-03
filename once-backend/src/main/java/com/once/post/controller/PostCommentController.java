package com.once.post.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.post.dto.CommentCreateRequest;
import com.once.post.dto.CommentResponse;
import com.once.post.service.PostCommentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/posts/{postId}/comments")
public class PostCommentController {

    private final PostCommentService commentService;

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CommentCreateRequest req
    ) {
        return ResponseEntity.ok(commentService.createComment(groupId, postId, user.getId(), req));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long groupId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CommentCreateRequest req
    ) {
        return ResponseEntity.ok(commentService.updateComment(groupId, commentId, user.getId(), req));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long groupId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        commentService.deleteComment(groupId, commentId, user.getId());
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}