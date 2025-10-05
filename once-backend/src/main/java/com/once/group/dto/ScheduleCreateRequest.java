package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleCreateRequest {
    private String title;
    private String date;
    private String time;
    private String description;
}
