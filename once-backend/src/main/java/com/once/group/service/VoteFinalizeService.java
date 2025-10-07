package com.once.group.service;

import com.once.group.domain.Group;
import com.once.group.domain.Schedule;
import com.once.group.domain.Vote;
import com.once.group.domain.VoteOption;
import com.once.group.dto.ScheduleResponse;
import com.once.group.repository.GroupRepository;
import com.once.group.repository.ScheduleRepository;
import com.once.group.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoteFinalizeService {

    private final VoteRepository voteRepository;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Map<String, Object> finalizeVote(Long groupId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        if (vote.getGroup() == null || !vote.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 투표는 해당 그룹에 속하지 않습니다.");
        }

        // 득표수 기준으로 최다 득표 날짜 찾기
        VoteOption topOption = vote.getOptions().stream()
                .max(Comparator.comparingInt(VoteOption::getVoteCount))
                .orElseThrow(() -> new EntityNotFoundException("투표 옵션이 없습니다."));

        Group group = vote.getGroup();

        // 일정 생성
        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setTitle(vote.getTitle());
        schedule.setDate(topOption.getOptionDate());
        schedule.setTime(LocalTime.of(10, 0)); // 기본 시간 설정
        schedule.setDescription("투표로 자동 등록된 일정입니다.");

        Schedule saved = scheduleRepository.save(schedule);

        // 응답 구성
        Map<String, Object> data = new HashMap<>();
        data.put("scheduleId", saved.getId());
        data.put("groupId", groupId);
        data.put("title", saved.getTitle());
        data.put("date", topOption.getOptionDate().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "최다 득표 날짜로 일정이 등록되었습니다.");
        result.put("data", data);

        return result;
    }
}
