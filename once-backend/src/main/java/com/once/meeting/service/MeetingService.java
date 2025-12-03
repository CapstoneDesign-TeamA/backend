package com.once.meeting.service;

import com.once.group.repository.GroupMemberRepository;
import com.once.meeting.domain.Meeting;
import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import com.once.meeting.dto.MeetingCreateRequest;
import com.once.meeting.dto.MeetingUpdateRequest;
import com.once.meeting.dto.MeetingResponse;
import com.once.meeting.repository.MeetingParticipantRepository;
import com.once.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final GroupMemberRepository groupMemberRepository;

    // ========================================
    // 모임 생성
    // ========================================
    public MeetingResponse createMeeting(Long groupId, Long creatorId, MeetingCreateRequest req) {

        // 그룹 멤버 여부 확인
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, creatorId)) {
            throw new RuntimeException("그룹 멤버가 아닙니다.");
        }

        LocalDate start = req.getStartDate();
        LocalDate end = req.getEndDate();
        String time = req.getTime();

        if (start == null) throw new RuntimeException("startDate는 필수입니다.");

        if (end == null) end = start;
        if (!start.equals(end)) time = null;

        Meeting meeting = Meeting.builder()
                .groupId(groupId)
                .creatorId(creatorId)
                .title(req.getTitle())
                .description(req.getDescription())
                .startDate(start)
                .endDate(end)
                .time(time)
                .location(req.getLocation())
                .build();

        Meeting saved = meetingRepository.save(meeting);

        // 생성자는 자동 참여 ACCEPTED
        participantRepository.save(
                MeetingParticipant.builder()
                        .meetingId(saved.getId())
                        .userId(creatorId)
                        .status(ParticipationStatus.ACCEPTED)
                        .build()
        );

        int count = participantRepository.countByMeetingIdAndStatus(saved.getId(), ParticipationStatus.ACCEPTED);

        // creator = 무조건 ACCEPTED
        return MeetingResponse.from(saved, count, ParticipationStatus.ACCEPTED.name());
    }

    // ========================================
    // 모임 목록 조회 (myStatus 포함)
    // ========================================
    public List<MeetingResponse> getMeetings(Long groupId, Long userId) {

        List<Meeting> meetings = meetingRepository.findByGroupId(groupId);

        return meetings.stream()
                .map(m -> {

                    // 참여 인원 수 (ACCEPTED만 카운트)
                    int count = participantRepository.countByMeetingIdAndStatus(
                            m.getId(),
                            ParticipationStatus.ACCEPTED
                    );

                    // 현재 로그인한 유저의 참여 상태
                    String myStatus = participantRepository
                            .findByMeetingIdAndUserId(m.getId(), userId)
                            .map(p -> p.getStatus().name())   // ACCEPTED or DECLINED
                            .orElse(null);                    // 참여 안 했으면 null

                    return MeetingResponse.from(m, count, myStatus);
                })
                .toList();
    }

    // ========================================
    // 모임 수정 (creator만 가능)
    // ========================================
    public MeetingResponse updateMeeting(
            Long groupId,
            Long meetingId,
            Long userId,
            MeetingUpdateRequest req
    ) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보가 일치하지 않습니다.");
        }

        if (!meeting.getCreatorId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        LocalDate start = req.getStartDate();
        LocalDate end = req.getEndDate();
        String time = req.getTime();

        if (start == null) {
            throw new RuntimeException("startDate는 필수입니다.");
        }

        if (end == null) end = start;
        if (!start.equals(end)) time = null;

        meeting.setTitle(req.getTitle());
        meeting.setDescription(req.getDescription());
        meeting.setStartDate(start);
        meeting.setEndDate(end);
        meeting.setTime(time);
        meeting.setLocation(req.getLocation());

        Meeting updated = meetingRepository.save(meeting);

        int count = participantRepository.countByMeetingIdAndStatus(meetingId, ParticipationStatus.ACCEPTED);

        // 업데이트 후도 로그인 유저의 참여 상태 다시 계산
        String myStatus = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .map(p -> p.getStatus().name())
                .orElse(null);

        return MeetingResponse.from(updated, count, myStatus);
    }

    // ========================================
    // 모임 삭제 (creator만 가능)
    // ========================================
    public void deleteMeeting(Long groupId, Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보 불일치");
        }

        if (!meeting.getCreatorId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        participantRepository.deleteByMeetingId(meetingId);
        meetingRepository.delete(meeting);
    }
}