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

    private String startDate;  // 프론트는 String으로 받기 때문에 유지
    private String endDate;
    private String time;
    private String location;

    private int participantCount;

    public static MeetingResponse from(Meeting meeting, int participantCount) {
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

        return res;
    }
}