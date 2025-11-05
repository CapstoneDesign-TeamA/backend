package com.once.group.service;

import com.once.group.domain.Vote;
import com.once.group.domain.VoteOption;
import com.once.group.dto.VoteResultResponse;
import com.once.group.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteResultService {

    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public VoteResultResponse getVoteResults(Long groupId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        if (vote.getGroup() == null || !vote.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 투표는 해당 그룹에 속하지 않습니다.");
        }

        VoteResultResponse response = new VoteResultResponse();
        response.setVoteId(vote.getId());
        response.setTitle(vote.getTitle());

        List<VoteResultResponse.ResultItem> results = vote.getOptions().stream()
                .map(opt -> {
                    VoteResultResponse.ResultItem r = new VoteResultResponse.ResultItem();
                    r.setOptionDate(opt.getOptionDate().toString());
                    r.setVoteCount(opt.getVoteCount());
                    return r;
                })
                .collect(Collectors.toList());

        response.setResults(results);
        return response;
    }
}
