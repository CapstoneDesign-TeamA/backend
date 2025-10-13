package com.once.group.controller;

import com.once.group.service.VoteFinalizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/votes")
public class VoteFinalizeController {

    private final VoteFinalizeService voteFinalizeService;
    
    // 일정 자동 등록
    @PostMapping("/{voteId}/finalize")
    public ResponseEntity<Map<String, Object>> finalizeVote(
            @PathVariable Long groupId,
            @PathVariable Long voteId) {

        Map<String, Object> response = voteFinalizeService.finalizeVote(groupId, voteId);
        return ResponseEntity.ok(response);
    }
}
