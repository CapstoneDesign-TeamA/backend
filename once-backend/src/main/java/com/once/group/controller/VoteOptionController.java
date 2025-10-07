package com.once.group.controller;

import com.once.group.service.VoteOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/votes/{voteId}/select")
public class VoteOptionController {

    private final VoteOptionService voteOptionService;
    
    // 투표하기
    @PostMapping("/{optionId}")
    public ResponseEntity<Map<String, Object>> selectVote(
            @PathVariable Long groupId,
            @PathVariable Long voteId,
            @PathVariable Long optionId,
            @RequestBody Map<String, Long> request) {

        Long userId = request.get("userId"); // 단순히 유저 ID 받음(임시)
        Map<String, Object> response = voteOptionService.selectVote(groupId, voteId, optionId, userId);

        return ResponseEntity.ok(response);
    }
}
