package com.once.group.controller;

import com.once.group.dto.AlbumCreateRequest;
import com.once.group.dto.AlbumResponse;
import com.once.group.service.AlbumService;
import com.once.group.service.AutoAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(
            @PathVariable Long groupId,
            @RequestBody AlbumCreateRequest request) {

        AlbumResponse album = albumService.createAlbum(groupId, request);

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
            @RequestBody com.once.group.dto.AlbumCreateRequest request) {

        AlbumResponse updated = albumService.updateAlbum(groupId, albumId, request);

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

        Map<String, Object> data = new HashMap<>();
        data.put("albumId", album.getAlbumId());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "모임 종료 후 앨범이 자동 생성되었습니다.");
        result.put("data", data);

        return ResponseEntity.ok(result);
    }

}
