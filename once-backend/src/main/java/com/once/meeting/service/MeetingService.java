package com.once.meeting.service;

import com.once.group.repository.GroupMemberRepository;
import com.once.meeting.domain.Meeting;
import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import com.once.meeting.dto.MeetingCreateRequest;
import com.once.meeting.dto.MeetingResponse;
import com.once.meeting.repository.MeetingParticipantRepository;
import com.once.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final GroupMemberRepository groupMemberRepository;

    public MeetingResponse createMeeting(Long groupId, Long creatorId, MeetingCreateRequest req) {

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, creatorId)) {
            throw new RuntimeException("그룹 멤버가 아님");
        }

        Meeting meeting = Meeting.builder()
                .groupId(groupId)
                .creatorId(creatorId)
                .title(req.getTitle())
                .description(req.getDescription())
                .date(req.getDate())
                .time(req.getTime())
                .location(req.getLocation())
                .minMembers(req.getMinMembers())
                .maxMembers(req.getMaxMembers())
                .build();

        Meeting saved = meetingRepository.save(meeting);


        MeetingParticipant creatorParticipant = MeetingParticipant.builder()
                .meetingId(saved.getId())
                .userId(creatorId)
                .status(ParticipationStatus.ACCEPTED)
                .build();

        participantRepository.save(creatorParticipant);

        return MeetingResponse.from(saved);
    }

    public void deleteMeeting(Long groupId, Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임 없음"));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보 불일치");
        }

        if (!meeting.getCreatorId().equals(userId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        participantRepository.deleteByMeetingId(meetingId);
        meetingRepository.delete(meeting);
    }
}