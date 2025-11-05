package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GroupDetailResponse {
    private Long groupId;
    private String name;
    private List<String> members; // 구성원 이름 목록
    private List<ScheduleResponse> schedules; // 일정 목록
    private List<String> albums; // 앨범 목록
}
