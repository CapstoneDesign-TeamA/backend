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
@RequestMapping("/calendar") // 공통 경로 설정
public class CalendarController {

    private final CalendarService calendarService;

    // 신규 일정 등록
    @PostMapping
    public ResponseEntity<ScheduleCreateResponse> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        ScheduleCreateResponse response = calendarService.createSchedule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 일정 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleUpdateResponse> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleUpdateRequest request) {
        ScheduleUpdateResponse response = calendarService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(response);
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<GeneralResponse> deleteSchedule(@PathVariable Long scheduleId) {
        calendarService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(new GeneralResponse("일정이 성공적으로 삭제되었습니다."));
    }

    // 월 단위 일정 조회
    @GetMapping
    public ResponseEntity<MonthlyScheduleResponse> getMonthlySchedules(@RequestParam int year, @RequestParam int month) {
        MonthlyScheduleResponse response = calendarService.getMonthlySchedules(year, month);
        return ResponseEntity.ok(response);
    }

    // 특정 날짜 일정 목록 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<DailyScheduleResponse> getDailySchedules(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DailyScheduleResponse response = calendarService.getDailySchedules(date);
        return ResponseEntity.ok(response);
    }

    // 일정 상세 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailResponse> getScheduleDetails(@PathVariable Long scheduleId) {
        // PUT, DELETE의 {scheduleId}와 경로가 겹치지 않도록 GET 요청만 받습니다.
        if (isNumeric(String.valueOf(scheduleId))) {
            ScheduleDetailResponse response = calendarService.getScheduleDetails(scheduleId);
            return ResponseEntity.ok(response);
        }
        // 이 부분은 Spring이 알아서 처리하지만, 명시적으로 구분할 수도 있습니다.
        return ResponseEntity.badRequest().build();
    }

    // 숫자 판별을 위한 간단한 헬퍼 메서드
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }
}