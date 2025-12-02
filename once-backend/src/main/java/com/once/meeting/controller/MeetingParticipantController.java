package com.once.meeting.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.meeting.dto.ParticipantResponse;
import com.once.meeting.service.MeetingParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/meetings/{meetingId}")
@RequiredArgsConstructor
public class MeetingParticipantController {

    private final MeetingParticipantService participantService;

    @PostMapping("/participate")
    public ResponseEntity<String> participate(
            @PathVariable Long groupId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        participantService.accept(groupId, meetingId, user.getId());
        return ResponseEntity.ok("참여 완료");
    }

    @PostMapping("/decline")
    public ResponseEntity<String> decline(
            @PathVariable Long groupId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        participantService.decline(groupId, meetingId, user.getId());
        return ResponseEntity.ok("불참 처리 완료");
    }

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