package com.once.meeting.repository;

import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {


    List<MeetingParticipant> findByMeetingId(Long meetingId);


    Optional<MeetingParticipant> findByMeetingIdAndUserId(Long meetingId, Long userId);


    int countByMeetingIdAndStatus(Long meetingId, ParticipationStatus status);


    void deleteByMeetingId(Long meetingId);


    List<MeetingParticipant> findByMeetingIdAndStatus(Long meetingId, ParticipationStatus status);
}