package com.once.meeting.service;

import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import com.once.meeting.dto.ParticipantResponse;
import com.once.meeting.repository.MeetingParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingParticipantService {

    private final MeetingParticipantRepository participantRepository;

    /**
     * 참여 처리
     */
    public void accept(Long groupId, Long meetingId, Long userId) {

        // 기존 기록 확인
        MeetingParticipant existing =
                participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                        .orElse(null);

        if (existing != null) {
            // 이미 참여 중이라면 에러 발생
            if (existing.getStatus() == ParticipationStatus.ACCEPTED) {
                throw new IllegalStateException("ALREADY_PARTICIPATED");
            }

            // 기존이 DECLINED → 참여로 변경
            existing.setStatus(ParticipationStatus.ACCEPTED);
            participantRepository.save(existing);
            return;
        }

        // 처음 참여하는 경우 → 새로운 엔트리 삽입
        MeetingParticipant p = MeetingParticipant.builder()
                .meetingId(meetingId)
                .userId(userId)
                .status(ParticipationStatus.ACCEPTED)
                .build();

        participantRepository.save(p);
    }

    /**
     * 불참 처리
     */
    public void decline(Long groupId, Long meetingId, Long userId) {

        MeetingParticipant existing =
                participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                        .orElse(null);

        if (existing != null) {
            if (existing.getStatus() == ParticipationStatus.DECLINED) {
                throw new IllegalStateException("ALREADY_DECLINED");
            }

            existing.setStatus(ParticipationStatus.DECLINED);
            participantRepository.save(existing);
            return;
        }

        MeetingParticipant p = MeetingParticipant.builder()
                .meetingId(meetingId)
                .userId(userId)
                .status(ParticipationStatus.DECLINED)
                .build();

        participantRepository.save(p);
    }

    /**
     * 참여자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getParticipants(Long groupId, Long meetingId) {
        return participantRepository.findByMeetingId(meetingId)
                .stream()
                .map(ParticipantResponse::from)
                .toList();
    }
}