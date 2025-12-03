package com.once.meeting.dto;

import com.once.meeting.domain.Meeting;
import lombok.Getter;
import lombok.Setter;

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

    private int participantCount;

    // ★ 추가된 필드
    private String myStatus;   // "ACCEPTED" | "DECLINED" | null

    // ★ myStatus까지 포함한 새로운 팩토리 메서드
    public static MeetingResponse from(
            Meeting meeting,
            int participantCount,
            String myStatus
    ) {
        MeetingResponse res = new MeetingResponse();

        res.setId(meeting.getId());
        res.setGroupId(meeting.getGroupId());
        res.setCreatorId(meeting.getCreatorId());
        res.setTitle(meeting.getTitle());
        res.setDescription(meeting.getDescription());

        res.setStartDate(
                meeting.getStartDate() != null ? meeting.getStartDate().toString() : null
        );
        res.setEndDate(
                meeting.getEndDate() != null ? meeting.getEndDate().toString() : null
        );

        res.setTime(meeting.getTime());
        res.setLocation(meeting.getLocation());
        res.setParticipantCount(participantCount);

        // ★ 추가
        res.setMyStatus(myStatus);

        return res;
    }
}