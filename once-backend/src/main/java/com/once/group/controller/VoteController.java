package com.once.group.controller;

import com.once.group.dto.VoteCreateRequest;
import com.once.group.dto.VoteCreateResponse;
import com.once.group.dto.VoteDetailResponse;
import com.once.group.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/votes")
public class VoteController {

    private final VoteService voteService;

    // 투표 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> createVote(
            @PathVariable Long groupId,
            @RequestBody VoteCreateRequest request) {

        VoteCreateResponse response = voteService.createVote(groupId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "투표가 등록되었습니다.");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    // 투표 조회
    @GetMapping("/{voteId}")
    public ResponseEntity<Map<String, Object>> getVoteDetail(
            @PathVariable Long groupId,
            @PathVariable Long voteId) {

        VoteDetailResponse response = voteService.getVoteDetail(groupId, voteId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", response);
        return ResponseEntity.ok(result);
    }
}
