package com.once.group.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.group.dto.*;
import com.once.group.service.GroupService;
import com.once.group.service.ImageUploadService;
import com.once.group.service.InviteService;
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
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final ImageUploadService imageUploadService;
    private final InviteService inviteService;


    /**
     * 그룹 생성
     * - 이미지 파일은 선택적 업로드
     * - name, description, imageUrl 기반 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createGroup(
            @RequestPart("name") String name,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        if (description == null) description = "";

        // 대표 이미지 업로드 처리
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageUploadService.uploadImage(file);
        }

        GroupResponse response = groupService.createGroup(name, description, imageUrl);

        Map<String, Object> data = new HashMap<>();
        data.put("groupId", response.getGroupId());
        data.put("name", response.getName());
        data.put("createdAt", response.getCreatedAt());
        data.put("imageUrl", response.getImageUrl());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "그룹이 생성되었습니다.");
        result.put("data", data);

        return ResponseEntity.ok(result);
    }


    /**
     * 전체 그룹 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        List<GroupResponse> groups = groupService.getAllGroups();

        Map<String, Object> result = new HashMap<>();
        result.put("data", groups);
        return ResponseEntity.ok(result);
    }


    /**
     * 내가 속한 그룹 조회
     */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyGroups() {
        List<GroupResponse> groups = groupService.getMyGroups();

        Map<String, Object> result = new HashMap<>();
        result.put("data", groups);
        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 상세 조회
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroupById(@PathVariable Long groupId) {
        GroupDetailResponse detail = groupService.getGroupDetail(groupId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", detail);
        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 수정
     * - 이미지 변경 시 file 업로드
     * - imageUrl = null이면 기존 이미지 유지
     */
    @PutMapping(value = "/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateGroup(
            @PathVariable Long groupId,
            @RequestPart("name") String name,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        if (description == null) description = "";

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageUploadService.uploadImage(file);
        }

        GroupResponse response = groupService.updateGroup(groupId, name, description, imageUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "그룹 정보가 수정되었습니다.");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 삭제
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId();
        groupService.deleteGroup(groupId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "그룹이 삭제되었습니다.");
        result.put("data", null);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 나가기
     */
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Map<String, Object>> leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId();
        groupService.leaveGroup(groupId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "그룹에서 나갔습니다.");
        result.put("data", null);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 초대
     */
    @PostMapping("/{groupId}/invites")
    public ResponseEntity<Map<String, Object>> inviteMember(
            @PathVariable Long groupId,
            @RequestBody GroupInviteRequest request
    ) {
        request.setGroupId(groupId);
        GroupInviteResponse response = groupService.inviteMember(request);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "그룹 초대 완료");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 일정 등록
     */
    @PostMapping("/{groupId}/schedules")
    public ResponseEntity<Map<String, Object>> createSchedule(
            @PathVariable Long groupId,
            @RequestBody ScheduleCreateRequest request
    ) {
        ScheduleResponse response = groupService.createSchedule(groupId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "일정이 등록되었습니다.");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 일정 조회
     */
    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<Map<String, Object>> getSchedulesByGroup(@PathVariable Long groupId) {
        List<ScheduleResponse> schedules = groupService.getSchedulesByGroup(groupId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", schedules);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 일정 수정
     */
    @PutMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<Map<String, Object>> updateSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleCreateRequest request
    ) {
        ScheduleResponse response = groupService.updateSchedule(groupId, scheduleId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "일정이 수정되었습니다.");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }


    /**
     * 그룹 일정 삭제
     */
    @DeleteMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<Map<String, Object>> deleteSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId
    ) {
        groupService.deleteSchedule(groupId, scheduleId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "일정이 삭제되었습니다.");
        result.put("data", null);

        return ResponseEntity.ok(result);
    }


    /**
     * 초대 링크 생성
     */
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<?> createInviteLink(@PathVariable Long groupId) {
        try {
            String token = inviteService.createInviteLink(groupId);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("message", "초대 링크가 생성되었습니다.");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    /**
     * 초대 정보 조회
     */
    @GetMapping("/invite/info")
    public ResponseEntity<?> getInviteInfo(@RequestParam String token) {
        try {
            GroupInviteInfo info = inviteService.getInviteInfo(token);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    /**
     * 초대 수락
     */
    @PostMapping("/invite/accept")
    public ResponseEntity<?> acceptInvite(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            com.once.user.domain.User user = new com.once.user.domain.User();
            user.setId(userDetails.getId());
            user.setEmail(userDetails.getUsername());

            inviteService.acceptInvite(token, user);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "그룹에 성공적으로 가입되었습니다.");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}