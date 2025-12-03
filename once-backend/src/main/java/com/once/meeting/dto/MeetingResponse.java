package com.once.meeting.dto;

import com.once.meeting.domain.Meeting;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeetingResponse {

    private Long id;
    private Long groupId;
    private Long creatorId;
    private String title;
    private String description;

    private String startDate;
    private String endDate;
    private String time;
    private String location;

    // 참여 인원 수 (ACCEPTED만)
    private int participantCount;

    // 현재 로그인 유저의 참여 상태 (ACCEPTED | DECLINED | null)
    private String myStatus;

    // 참여자 / 불참자 이름 목록 (nickname 우선, 없으면 username)
    private List<String> participants;
    private List<String> declined;

    /**
     * MeetingResponse 팩토리 메서드
     */
    public static MeetingResponse from(
            Meeting meeting,
            int participantCount,
            String myStatus,
            List<String> participants,
            List<String> declined
    ) {
        MeetingResponse res = new MeetingResponse();

        res.id = meeting.getId();
        res.groupId = meeting.getGroupId();
        res.creatorId = meeting.getCreatorId();
        res.title = meeting.getTitle();
        res.description = meeting.getDescription();

        res.startDate = meeting.getStartDate() != null
                ? meeting.getStartDate().toString()
                : null;

        res.endDate = meeting.getEndDate() != null
                ? meeting.getEndDate().toString()
                : null;

        res.time = meeting.getTime();
        res.location = meeting.getLocation();

        res.participantCount = participantCount;
        res.myStatus = myStatus;

        // 참여/불참자
        res.participants = participants;
        res.declined = declined;

        return res;
    }
}