package com.once.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingCreateRequest {

    private String title;
    private String description;

    private String date;
    private String time;
    private String location;

    private Integer minMembers;
    private Integer maxMembers;
}