package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDetailResponse {

    private Long groupId;
    private String name;
    private String description;
    private String imageUrl;
    private List<String> members;
    private List<ScheduleResponse> schedules;
    private List<String> albums;
}