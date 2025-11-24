package com.once.group.controller;

import com.once.group.dto.AlbumResponse;
import com.once.group.service.AlbumService;
import com.once.group.service.AutoAlbumService;
import com.once.group.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
     * 앨범 등록 (그룹 앨범 사진 업로드)
     * 프론트에서 보내는 FormData:
     *  - title: string
     *  - description: string (빈 문자열 가능)
     *  - files: File[], 여러 장 업로드 가능
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createAlbum(
            @PathVariable Long groupId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        if (description == null) {
            description = "";
        }

        // 다중 이미지 업로드 처리
        List<String> imageUrls = null;
        if (files != null && !files.isEmpty()) {
            imageUrls = imageUploadService.uploadImages(files); //  다중 업로드 메서드
        }

        // 앨범 DB 등록
        AlbumResponse album = albumService.createAlbum(groupId, title, description, imageUrls);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 등록되었습니다.");
        result.put("data", album);
        return ResponseEntity.ok(result);
    }

    /**
     * 앨범 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAlbums(@PathVariable Long groupId) {
        List<AlbumResponse> albums = albumService.getAlbumsByGroup(groupId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", albums);
        return ResponseEntity.ok(result);
    }

    /**
     * 앨범 수정 (사진 여러 장 교체 가능)
     * - 파일을 안 보내면 텍스트만 수정됨
     * - 파일을 보내면 기존 사진 모두 삭제 후 새 사진으로 교체
     */
    @PutMapping(value = "/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateAlbum(
            @PathVariable Long groupId,
            @PathVariable Long albumId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        if (description == null) {
            description = "";
        }

        // 다중 파일 업로드 → URL 리스트 생성
        List<String> imageUrls = null;
        if (files != null && !files.isEmpty()) {
            imageUrls = imageUploadService.uploadImages(files); // 다중 이미지 업로드
        }

        AlbumResponse updated = albumService.updateAlbum(groupId, albumId, title, description, imageUrls);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 수정되었습니다.");
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    /**
     * 앨범 삭제
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
     * 모임 종료 후 자동 앨범 생성 (아직 단일 이미지 구조)
     */
    @PostMapping("/auto")
    public ResponseEntity<Map<String, Object>> createAutoAlbum(
            @PathVariable Long groupId,
            @RequestBody Map<String, Long> request
    ) {
        Long meetingId = request.get("meetingId");
        AlbumResponse album = autoAlbumService.createAutoAlbum(groupId, meetingId);

        // 이미지가 없을 경우 기본 이미지 채우기
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
}