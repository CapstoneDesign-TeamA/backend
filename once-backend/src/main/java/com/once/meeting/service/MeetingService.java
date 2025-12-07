package com.once.meeting.service;

import com.once.calendar.repository.CalendarScheduleRepository;
import com.once.group.repository.GroupMemberRepository;
import com.once.meeting.domain.Meeting;
import com.once.meeting.domain.MeetingParticipant;
import com.once.meeting.domain.ParticipationStatus;
import com.once.meeting.dto.MeetingCreateRequest;
import com.once.meeting.dto.MeetingUpdateRequest;
import com.once.meeting.dto.MeetingResponse;
import com.once.meeting.repository.MeetingParticipantRepository;
import com.once.meeting.repository.MeetingRepository;
import com.once.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final CalendarScheduleRepository calendarScheduleRepository;



    // username 또는 nickname으로 이름 반환 (nickname 우선)
    private String resolveName(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getNickname() != null ? u.getNickname() : u.getUsername())
                .orElse("unknown");
    }



    // ========================================
    // 모임 생성
    // ========================================
    public MeetingResponse createMeeting(Long groupId, Long creatorId, MeetingCreateRequest req) {

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

        // 작성자는 자동 ACCEPTED
        participantRepository.save(
                MeetingParticipant.builder()
                        .meetingId(saved.getId())
                        .userId(creatorId)
                        .status(ParticipationStatus.ACCEPTED)
                        .build()
        );

        int count = participantRepository.countByMeetingIdAndStatus(saved.getId(), ParticipationStatus.ACCEPTED);

        return MeetingResponse.from(saved, count, ParticipationStatus.ACCEPTED.name(), List.of(), List.of());
    }



    // ========================================
    // 모임 목록 조회 (참여자 이름 포함)
    // ========================================
    public List<MeetingResponse> getMeetings(Long groupId, Long userId) {

        List<Meeting> meetings = meetingRepository.findByGroupId(groupId);

        return meetings.stream()
                .map(m -> {

                    int participantCount = participantRepository.countByMeetingIdAndStatus(
                            m.getId(),
                            ParticipationStatus.ACCEPTED
                    );

                    String myStatus = participantRepository.findByMeetingIdAndUserId(m.getId(), userId)
                            .map(p -> p.getStatus().name())
                            .orElse(null);

                    List<String> participants = participantRepository
                            .findByMeetingIdAndStatus(m.getId(), ParticipationStatus.ACCEPTED)
                            .stream()
                            .map(p -> resolveName(p.getUserId()))
                            .toList();

                    List<String> declined = participantRepository
                            .findByMeetingIdAndStatus(m.getId(), ParticipationStatus.DECLINED)
                            .stream()
                            .map(p -> resolveName(p.getUserId()))
                            .toList();

                    return MeetingResponse.from(
                            m,
                            participantCount,
                            myStatus,
                            participants,
                            declined
                    );
                })
                .toList();
    }



    // ========================================
    // 모임 수정
    // ========================================
    @Transactional
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

        if (start == null) throw new RuntimeException("startDate는 필수입니다.");
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

        String myStatus = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .map(p -> p.getStatus().name())
                .orElse(null);

        List<String> participants = participantRepository
                .findByMeetingIdAndStatus(meetingId, ParticipationStatus.ACCEPTED)
                .stream()
                .map(p -> resolveName(p.getUserId()))
                .toList();

        List<String> declined = participantRepository
                .findByMeetingIdAndStatus(meetingId, ParticipationStatus.DECLINED)
                .stream()
                .map(p -> resolveName(p.getUserId()))
                .toList();

        return MeetingResponse.from(updated, count, myStatus, participants, declined);
    }



    // ========================================
    // 모임 삭제
    // ========================================
    @Transactional
    public void deleteMeeting(Long groupId, Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        if (!meeting.getGroupId().equals(groupId)) {
            throw new RuntimeException("그룹 정보 불일치");
        }

        if (!meeting.getCreatorId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // ✅ 모임과 연결된 캘린더 일정도 함께 삭제
        calendarScheduleRepository.deleteByMeetingId(meetingId);

        participantRepository.deleteByMeetingId(meetingId);
        meetingRepository.delete(meeting);
    }
}