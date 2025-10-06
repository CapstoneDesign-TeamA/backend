package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponse {
    private Long scheduleId;
    private Long groupId;
    private String title;
    private String date;
    private String time;
    private String description;
    private LocalDateTime createdAt;
}
