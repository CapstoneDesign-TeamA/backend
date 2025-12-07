/**
 * File: MeetingParticipantService.java
 * Description:
 *  - 모임 참여/불참 처리 서비스
 *  - 사용자의 참여 상태를 ACCEPTED / DECLINED 로 관리
 *  - 모임별 전체 참여자 목록 조회 기능 제공
 */

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

    // 참여 처리
    public void accept(Long groupId, Long meetingId, Long userId) {

        // 기존 기록 조회
        MeetingParticipant existing =
                participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                        .orElse(null);

        // 이미 참여 상태인 경우
        if (existing != null) {
            if (existing.getStatus() == ParticipationStatus.ACCEPTED) {
                throw new IllegalStateException("ALREADY_PARTICIPATED");
            }

            // 기존이 DECLINED이면 ACCEPTED로 변경
            existing.setStatus(ParticipationStatus.ACCEPTED);
            participantRepository.save(existing);
            return;
        }

        // 처음 참여하는 경우
        MeetingParticipant p = MeetingParticipant.builder()
                .meetingId(meetingId)
                .userId(userId)
                .status(ParticipationStatus.ACCEPTED)
                .build();

        participantRepository.save(p);
    }

    // 불참 처리
    public void decline(Long groupId, Long meetingId, Long userId) {

        // 기존 기록 조회
        MeetingParticipant existing =
                participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                        .orElse(null);

        // 이미 DECLINED 상태인 경우
        if (existing != null) {
            if (existing.getStatus() == ParticipationStatus.DECLINED) {
                throw new IllegalStateException("ALREADY_DECLINED");
            }

            existing.setStatus(ParticipationStatus.DECLINED);
            participantRepository.save(existing);
            return;
        }

        // 처음 불참 처리하는 경우
        MeetingParticipant p = MeetingParticipant.builder()
                .meetingId(meetingId)
                .userId(userId)
                .status(ParticipationStatus.DECLINED)
                .build();

        participantRepository.save(p);
    }

    // 참여자 목록 조회
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getParticipants(Long groupId, Long meetingId) {
        return participantRepository.findByMeetingId(meetingId)
                .stream()
                .map(ParticipantResponse::from)
                .toList();
    }
}