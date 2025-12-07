/**
 * File: MeetingUpdateRequest.java
 * Description:
 *  - 모임 수정 요청 DTO
 *  - 제목, 설명, 시작/종료 날짜, 시간, 장소를 수정할 때 사용
 *  - 날짜는 yyyy-MM-dd 형식으로 전달됨
 */

package com.once.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class MeetingUpdateRequest {

    private String title;         // 모임 제목
    private String description;   // 모임 설명

    @JsonProperty("startDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;  // 시작 날짜

    @JsonProperty("endDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;    // 종료 날짜

    private String time;          // 모임 시간
    private String location;      // 모임 장소
}