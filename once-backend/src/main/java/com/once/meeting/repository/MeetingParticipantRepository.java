/**
 * File: MeetingParticipantRepository.java
 * Description:
 *  - MeetingParticipant 엔티티에 대한 DB 접근을 제공하는 Repository
 *  - 모임 참여자 조회, 참여 여부 확인, 상태별 인원 수 조회 기능을 포함
 */

package com.once.meeting.repository;

import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    // 특정 모임의 전체 참여자 조회
    List<MeetingParticipant> findByMeetingId(Long meetingId);

    // 특정 모임에서 특정 사용자의 참여 정보 조회
    Optional<MeetingParticipant> findByMeetingIdAndUserId(Long meetingId, Long userId);

    // 특정 모임에서 특정 참여 상태를 가진 인원 수 반환
    int countByMeetingIdAndStatus(Long meetingId, ParticipationStatus status);

    // 모임 삭제 시 해당 모임의 참여 기록 전체 삭제
    void deleteByMeetingId(Long meetingId);

    // 특정 상태(ACCEPTED, DECLINED 등)의 참여자 목록 조회
    List<MeetingParticipant> findByMeetingIdAndStatus(Long meetingId, ParticipationStatus status);
}