/**
 * File: MeetingParticipantController.java
 * Description:
 *  - 모임 참여 및 불참 처리
 *  - 모임 참석자 목록 조회 기능 제공
 *  - 로그인 사용자 기반 동작 수행
 */

package com.once.meeting.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.meeting.dto.ParticipantResponse;
import com.once.meeting.service.MeetingParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups/{groupId}/meetings/{meetingId}")
@RequiredArgsConstructor
public class MeetingParticipantController {

    private final MeetingParticipantService participantService;

    // 모임 참여 처리
    @PostMapping("/participate")
    public ResponseEntity<?> participate(
            @PathVariable Long groupId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        participantService.accept(groupId, meetingId, user.getId());
        return ResponseEntity.ok(Map.of("message", "참여 완료"));
    }

    // 모임 불참 처리
    @PostMapping("/decline")
    public ResponseEntity<?> decline(
            @PathVariable Long groupId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        participantService.decline(groupId, meetingId, user.getId());
        return ResponseEntity.ok(Map.of("message", "불참 처리 완료"));
    }

    // 참석자 목록 조회
    @GetMapping("/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(
            @PathVariable Long groupId,
            @PathVariable Long meetingId
    ) {
        return ResponseEntity.ok(
                participantService.getParticipants(groupId, meetingId)
        );
    }
}