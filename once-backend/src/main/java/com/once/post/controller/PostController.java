/**
 * File: PostController.java
 * Description:
 *  - 그룹 내 게시글(피드) 생성, 조회, 수정, 삭제, 좋아요 기능을 제공하는 컨트롤러
 *  - 이미지 업로드 기반 FormData 요청과 JSON 기반 수정 요청을 처리하며
 *  - 로그인된 사용자의 식별 정보를 기반으로 게시글 작업을 수행함
 */

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

    // 게시글 생성 (이미지 포함 가능)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("content") String content,
            @RequestParam("type") String type,
            @RequestParam(value = "meetingId", required = false) Long meetingId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        Long userId = user.getId();
        PostResponse res = postService.createPost(
                groupId, userId, content, type, meetingId, files
        );

        return ResponseEntity.ok(res);
    }

    // 피드 조회
    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                postService.getFeed(groupId, user.getId())
        );
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        postService.deletePost(groupId, postId, user.getId());
        return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
    }

    // 게시글 수정
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

    // 좋아요 토글
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