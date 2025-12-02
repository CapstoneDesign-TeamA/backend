package com.once.meeting.service;

import com.once.group.repository.GroupMemberRepository;
import com.once.meeting.domain.Meeting;
import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import com.once.meeting.dto.ParticipantResponse;
import com.once.meeting.repository.MeetingParticipantRepository;
import com.once.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingParticipantService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final GroupMemberRepository groupMemberRepository;

    public void accept(Long groupId, Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임 없음"));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 불일치");
        }

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("그룹 멤버 아님");
        }

        participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .ifPresent(p -> { throw new RuntimeException("이미 참여됨"); });

        MeetingParticipant p = MeetingParticipant.builder()
                .meetingId(meetingId)
                .userId(userId)
                .status(ParticipationStatus.ACCEPTED)
                .build();

        participantRepository.save(p);
    }

    public void decline(Long groupId, Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임 없음"));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 불일치");
        }

        participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .ifPresentOrElse(
                        p -> {
                            p.setStatus(ParticipationStatus.DECLINED);
                            participantRepository.save(p);
                        },
                        () -> {
                            MeetingParticipant newP = MeetingParticipant.builder()
                                    .meetingId(meetingId)
                                    .userId(userId)
                                    .status(ParticipationStatus.DECLINED)
                                    .build();
                            participantRepository.save(newP);
                        }
                );
    }

    public List<ParticipantResponse> getParticipants(Long groupId, Long meetingId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임 없음"));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 불일치");
        }

        return participantRepository.findByMeetingId(meetingId)
                .stream()
                .map(ParticipantResponse::from)
                .toList();
    }
}