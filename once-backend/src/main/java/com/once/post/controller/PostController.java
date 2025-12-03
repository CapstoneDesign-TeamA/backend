package com.once.post.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.post.dto.PostResponse;
import com.once.post.dto.PostUpdateRequest;
import com.once.post.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/posts")
public class PostController {

    private final PostService postService;

    // ============================================================
    // 게시글 생성 (FormData 기반 / multiple files)
    // ============================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user,

            @RequestParam("content") String content,
            @RequestParam("type") String type,
            @RequestParam(value = "meetingId", required = false) Long meetingId,

            @RequestParam(value = "files", required = false)
            List<MultipartFile> files
    ) throws IOException {

        Long userId = user.getId();

        PostResponse res = postService.createPost(
                groupId,
                userId,
                content,
                type,
                meetingId,
                files
        );

        return ResponseEntity.ok(res);
    }

    // ============================================================
    // 피드 조회
    // ============================================================
    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(postService.getFeed(groupId, user.getId()));
    }

    // ============================================================
    // 게시글 삭제
    // ============================================================
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        postService.deletePost(groupId, postId, user.getId());

        return ResponseEntity.ok(
                Map.of("message", "게시글이 삭제되었습니다.")
        );
    }

    // ============================================================
    // 게시글 수정(JSON)
    // ============================================================
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody PostUpdateRequest req
    ) {
        PostResponse updated = postService.updatePost(groupId, postId, user.getId(), req);
        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // 좋아요 토글
    // ============================================================
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                postService.toggleLike(groupId, postId, user.getId())
        );
    }
}