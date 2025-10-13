package com.once.group.service;

import com.once.group.domain.Group;
import com.once.group.domain.Vote;
import com.once.group.domain.VoteOption;
import com.once.group.dto.VoteCreateRequest;
import com.once.group.dto.VoteCreateResponse;
import com.once.group.dto.VoteDetailResponse;
import com.once.group.repository.GroupRepository;
import com.once.group.repository.VoteOptionRepository;
import com.once.group.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final GroupRepository groupRepository;
    private final VoteOptionRepository voteOptionRepository;

    public VoteCreateResponse createVote(Long groupId, VoteCreateRequest request) {
        Group group = groupRepository.findById(groupId).orElse(null);

        Vote vote = new Vote();
        vote.setGroup(group);
        vote.setTitle(request.getTitle());

        Vote savedVote = voteRepository.save(vote);

        // 여러 날짜를 옵션으로 저장
        List<VoteOption> options = new ArrayList<>();
        for (String dateStr : request.getDates()) {
            VoteOption option = new VoteOption();
            option.setVote(savedVote);
            option.setOptionDate(LocalDate.parse(dateStr));
            options.add(option);
        }

        voteOptionRepository.saveAll(options);
        savedVote.setOptions(options);

        VoteCreateResponse response = new VoteCreateResponse();
        response.setVoteId(savedVote.getId());
        return response;
    }

    @Transactional(readOnly = true)
    public VoteDetailResponse getVoteDetail(Long groupId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        // 그룹 검증 (선택적)
        if (vote.getGroup() == null || !vote.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 투표는 해당 그룹에 속하지 않습니다.");
        }

        VoteDetailResponse response = new VoteDetailResponse();
        response.setVoteId(vote.getId());
        response.setTitle(vote.getTitle());
        response.setCreatedAt(vote.getCreatedAt());

        List<VoteDetailResponse.OptionResponse> options = vote.getOptions().stream()
                .map(opt -> {
                    VoteDetailResponse.OptionResponse o = new VoteDetailResponse.OptionResponse();
                    o.setOptionDate(opt.getOptionDate().toString());
                    o.setVoteCount(opt.getVoteCount());
                    return o;
                })
                .collect(Collectors.toList());

        response.setOptions(options);
        return response;
    }
}
