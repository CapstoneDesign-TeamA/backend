package com.once.group.service;

import com.once.group.domain.VoteOption;
import com.once.group.repository.VoteOptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoteOptionService {

    private final VoteOptionRepository voteOptionRepository;

    @Transactional
    public Map<String, Object> selectVote(Long groupId, Long voteId, Long optionId, Long userId) {

        VoteOption option = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 선택지를 찾을 수 없습니다."));

        // 그룹-투표 검증 (선택)
        if (!option.getVote().getId().equals(voteId)
                || option.getVote().getGroup() == null
                || !option.getVote().getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 선택지는 해당 투표/그룹에 속하지 않습니다.");
        }

        // 투표 수 증가
        option.setVoteCount(option.getVoteCount() + 1);
        voteOptionRepository.save(option);

        Map<String, Object> data = new HashMap<>();
        data.put("voteId", voteId);
        data.put("optionId", optionId);
        data.put("voteCount", option.getVoteCount());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "투표가 완료되었습니다.");
        result.put("data", data);

        return result;
    }
}
