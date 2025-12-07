/**
 * File: ScheduleResponse.java
 * Description:
 *  - 그룹 일정 조회 응답 DTO
 *  - 일정 기본 정보 + 생성 시각 포함
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponse {

    private Long scheduleId;      // 일정 ID
    private Long groupId;         // 그룹 ID
    private String title;         // 일정 제목
    private String date;          // 날짜 (yyyy-MM-dd)
    private String time;          // 시간 (HH:mm)
    private String description;   // 일정 설명
    private LocalDateTime createdAt; // 생성 시각
}