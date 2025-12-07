/**
 * File: ScheduleCreateRequest.java
 * Description:
 *  - 그룹 일정 생성 요청 DTO
 *  - 날짜/시간을 문자열 기반으로 전달받음
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleCreateRequest {

    private String title;       // 일정 제목
    private String date;        // 일정 날짜 (yyyy-MM-dd)
    private String time;        // 일정 시간 (HH:mm)
    private String description; // 일정 설명
}