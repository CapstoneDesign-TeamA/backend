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
    private String date;
    private String time;
    private String location;
    private Integer minMembers;
    private Integer maxMembers;

    public static MeetingResponse from(Meeting meeting) {
        MeetingResponse res = new MeetingResponse();
        res.setId(meeting.getId());
        res.setGroupId(meeting.getGroupId());
        res.setCreatorId(meeting.getCreatorId());
        res.setTitle(meeting.getTitle());
        res.setDescription(meeting.getDescription());
        res.setDate(meeting.getDate());
        res.setTime(meeting.getTime());
        res.setLocation(meeting.getLocation());
        res.setMinMembers(meeting.getMinMembers());
        res.setMaxMembers(meeting.getMaxMembers());
        return res;
    }
}