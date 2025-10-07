package com.once.group.controller;

import com.once.group.dto.VoteResultResponse;
import com.once.group.service.VoteResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/votes")

// 투표 결과 조회
public class VoteResultController {

    private final VoteResultService voteResultService;

    @GetMapping("/{voteId}/results")
    public ResponseEntity<Map<String, Object>> getVoteResults(
            @PathVariable Long groupId,
            @PathVariable Long voteId) {

        VoteResultResponse response = voteResultService.getVoteResults(groupId, voteId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}
