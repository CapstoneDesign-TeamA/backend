package com.once.group.controller;

import com.once.common.service.ImageUploadService;
import com.once.group.dto.AlbumResponse;
import com.once.group.service.AlbumService;
import com.once.group.service.AutoAlbumService;
import lombok.RequiredArgsConstructor;
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

// 앨범 등록
public class AlbumController {

    private final AlbumService albumService;
    private final AutoAlbumService autoAlbumService;
    private final ImageUploadService imageUploadService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(
            @PathVariable Long groupId,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        String imageUrl = imageUploadService.uploadImage(file);
        AlbumResponse album = albumService.createAlbum(groupId, title, description, file);


        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 등록되었습니다.");
        result.put("data", album);
        return ResponseEntity.ok(result);
    }


    // 앨범 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAlbums(@PathVariable Long groupId) {
        List<AlbumResponse> albums = albumService.getAlbumsByGroup(groupId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", albums);
        return ResponseEntity.ok(result);
    }

    // 앨범 수정
    @PutMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> updateAlbum(
            @PathVariable Long groupId,
            @PathVariable Long albumId,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        String imageUrl = imageUploadService.uploadImage(file);
        AlbumResponse updated = albumService.updateAlbum(groupId, albumId, title, description, imageUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 수정되었습니다.");
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    // 앨범 삭제
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(
            @PathVariable Long groupId,
            @PathVariable Long albumId) {

        albumService.deleteAlbum(groupId, albumId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "앨범이 삭제되었습니다.");
        result.put("data", null);

        return ResponseEntity.ok(result);
    }

    // 모임 종료 후 자동 앨범 생성
    @PostMapping("/auto")
    public ResponseEntity<Map<String, Object>> createAutoAlbum(
            @PathVariable Long groupId,
            @RequestBody Map<String, Long> request) {

        Long meetingId = request.get("meetingId");
        AlbumResponse album = autoAlbumService.createAutoAlbum(groupId, meetingId);

        if (album.getImageUrl() == null) {
            album.setImageUrl("https://objectstorage.ca-toronto-1.oraclecloud.com/n/yzhu49nqu7rk/b/once-bucket/o/default_album.png");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("albumId", album.getAlbumId());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임 종료 후 앨범이 자동 생성되었습니다.");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

}
