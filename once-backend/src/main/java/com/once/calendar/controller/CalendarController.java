// src/main/java/com/once/calendar/controller/CalendarController.java
package com.once.calendar.controller;

import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }

    // 신규 일정 등록
    @PostMapping
    public ResponseEntity<ScheduleCreateResponse> createSchedule(
            @Valid @RequestBody ScheduleCreateRequest request) {
        ScheduleCreateResponse response = calendarService.createSchedule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 일정 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleUpdateResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateRequest request) {
        ScheduleUpdateResponse response = calendarService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(response);
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<GeneralResponse> deleteSchedule(@PathVariable Long scheduleId) {
        calendarService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(new GeneralResponse("일정이 성공적으로 삭제되었습니다."));
    }

    // 월 단위 일정 조회 (year/month 없으면 현재 날짜 기준)
    @GetMapping
    public ResponseEntity<MonthlyScheduleResponse> getMonthlySchedules(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();
        int m = (month != null) ? month : now.getMonthValue();

        MonthlyScheduleResponse response = calendarService.getMonthlySchedules(y, m);
        return ResponseEntity.ok(response);
    }

    // 특정 날짜 일정 목록 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<DailyScheduleResponse> getDailySchedules(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DailyScheduleResponse response = calendarService.getDailySchedules(date);
        return ResponseEntity.ok(response);
    }

    // 일정 상세 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailResponse> getScheduleDetails(@PathVariable Long scheduleId) {
        ScheduleDetailResponse response = calendarService.getScheduleDetails(scheduleId);
        return ResponseEntity.ok(response);
    }

    // 그룹 멤버들의 개인 일정 조회 (그룹 캘린더용)
    @GetMapping("/group/{groupId}/members")
    public ResponseEntity<MonthlyScheduleResponse> getGroupMembersSchedules(
            @PathVariable Long groupId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "month") Integer month) {

        // year, month 유효성 검증
        if (year == null || month == null) {
            throw new IllegalArgumentException("year와 month는 필수 파라미터입니다.");
        }

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month는 1~12 사이의 값이어야 합니다.");
        }

        MonthlyScheduleResponse response = calendarService.getGroupMembersSchedules(groupId, year, month);
        return ResponseEntity.ok(response);
    }
}