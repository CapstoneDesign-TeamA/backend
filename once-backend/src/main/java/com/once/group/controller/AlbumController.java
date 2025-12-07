/**
 * File: AlbumController.java
 * Description:
 *  - 그룹 앨범 생성/조회/수정/삭제 기능 제공
 *  - 모임 종료 후 자동 생성 앨범 기능 포함
 */

package com.once.group.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.group.dto.AlbumResponse;
import com.once.group.service.AlbumService;
import com.once.group.service.AutoAlbumService;
import com.once.group.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups/{groupId}/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final AutoAlbumService autoAlbumService;
    private final ImageUploadService imageUploadService;

    /**
     * 그룹 앨범 등록
     * - title, description, file(FormData) 기반으로 앨범 이미지 업로드 및 DB 저장
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createAlbum(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        if (description == null) description = "";

        // 이미지 업로드 실행
        String imageUrl = imageUploadService.uploadImage(file);

        // 앨범 DB 저장
        AlbumResponse album = albumService.createAlbum(
                groupId, user.getId(), title, description, imageUrl
        );

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 등록되었습니다.");
        result.put("data", album);
        return ResponseEntity.ok(result);
    }

    /**
     * 그룹 앨범 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAlbums(@PathVariable Long groupId) {

        List<AlbumResponse> albums = albumService.getAlbumsByGroup(groupId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", albums);
        return ResponseEntity.ok(result);
    }

    /**
     * 그룹 앨범 수정
     * - title, description만 수정하는 경우 file 없이 요청 가능
     * - file이 존재하면 이미지 새로 업로드
     */
    @PutMapping(value = "/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateAlbum(
            @PathVariable Long groupId,
            @PathVariable Long albumId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        if (description == null) description = "";

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageUploadService.uploadImage(file); // 새로운 이미지 업로드
        }

        AlbumResponse updated = albumService.updateAlbum(
                groupId, albumId, user.getId(), title, description, imageUrl
        );

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 수정되었습니다.");
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    /**
     * 그룹 앨범 삭제
     */
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(
            @PathVariable Long groupId,
            @PathVariable Long albumId
    ) {

        albumService.deleteAlbum(groupId, albumId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 삭제되었습니다.");
        result.put("data", null);
        return ResponseEntity.ok(result);
    }

    /**
     * 모임 종료 후 자동 앨범 생성
     * - meetingId 기반
     * - 자동 생성 시 이미지가 없으면 기본 이미지로 설정
     */
    @PostMapping("/auto")
    public ResponseEntity<Map<String, Object>> createAutoAlbum(
            @PathVariable Long groupId,
            @RequestBody Map<String, Long> request
    ) {

        Long meetingId = request.get("meetingId");

        // 자동 생성된 앨범 정보
        AlbumResponse album = autoAlbumService.createAutoAlbum(groupId, meetingId);

        // 이미지 없으면 기본 이미지 적용
        if (album.getImageUrl() == null) {
            album.setImageUrl(
                    "https://objectstorage.ca-toronto-1.oraclecloud.com/n/yzhu49nqu7rk/b/once-bucket/o/default_album.png"
            );
        }

        Map<String, Object> data = new HashMap<>();
        data.put("albumId", album.getAlbumId());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임 종료 후 앨범이 자동 생성되었습니다.");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    /**
     * 이미지 URL 기준 앨범 삭제
     */
    @DeleteMapping("/by-url")
    public ResponseEntity<Map<String, Object>> deleteAlbumByImageUrl(
            @PathVariable Long groupId,
            @RequestParam("imageUrl") String imageUrl
    ) {

        albumService.deleteAlbumByImageUrl(groupId, imageUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 삭제되었습니다.");
        result.put("data", null);
        return ResponseEntity.ok(result);
    }
}