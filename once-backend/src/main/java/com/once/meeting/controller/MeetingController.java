/**
 * File: MeetingController.java
 * Description:
 *  - 그룹 내 모임 생성, 조회, 수정, 삭제 처리
 *  - 로그인 사용자 기반 권한 검증 수행
 */

package com.once.meeting.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.meeting.dto.MeetingCreateRequest;
import com.once.meeting.dto.MeetingUpdateRequest;
import com.once.meeting.dto.MeetingResponse;
import com.once.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    // 모임 목록 조회 (사용자 참여 상태 포함)
    @GetMapping
    public ResponseEntity<?> getMeetings(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId();
        return ResponseEntity.ok(meetingService.getMeetings(groupId, userId));
    }

    // 모임 생성
    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MeetingCreateRequest request
    ) {
        Long userId = user.getId();
        MeetingResponse response = meetingService.createMeeting(groupId, userId, request);
        return ResponseEntity.ok(response);
    }

    // 모임 수정 (작성자만 가능)
    @PutMapping("/{meetingId}")
    public ResponseEntity<MeetingResponse> updateMeeting(
            @PathVariable Long groupId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MeetingUpdateRequest request
    ) {
        Long userId = user.getId();
        MeetingResponse response =
                meetingService.updateMeeting(groupId, meetingId, userId, request);

        return ResponseEntity.ok(response);
    }

    // 모임 삭제 (작성자만 가능)
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(
            @PathVariable Long groupId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId();
        meetingService.deleteMeeting(groupId, meetingId, userId);
        return ResponseEntity.ok("모임이 취소되었습니다.");
    }
}