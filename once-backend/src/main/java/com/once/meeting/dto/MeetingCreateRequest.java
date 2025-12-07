/**
 * File: MeetingCreateRequest.java
 * Description:
 *  - 모임 생성 요청 DTO
 *  - 제목, 설명, 기간(startDate~endDate), 시간, 장소 정보를 포함
 *  - 날짜는 yyyy-MM-dd 형식으로 매핑됨
 */

package com.once.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class MeetingCreateRequest {

    private String title; // 모임 제목
    private String description; // 모임 설명

    @JsonProperty("startDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 모임 시작 날짜

    @JsonProperty("endDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // 모임 종료 날짜

    private String time; // 모임 시간 (HH:mm 등 문자열)
    private String location; // 모임 장소
}