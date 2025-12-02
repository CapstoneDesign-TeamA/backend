package com.once.calendar.controller;

import com.once.calendar.service.CalendarGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/group")
public class CalendarGroupController {

    private final CalendarGroupService calendarGroupService;

    /**
     * 날짜별 그룹 멤버 busy-count 조회
     * 예: /calendar/group/8/busy-count?startDate=2025-12-01&endDate=2025-12-31
     */
    @GetMapping("/{groupId}/busy-count")
    public ResponseEntity<?> getBusyCount(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {

        Map<String, Integer> busyCountByDay =
                calendarGroupService.getBusyCountByDay(groupId, startDate, endDate);

        return ResponseEntity.ok(Map.of("busyCountByDay", busyCountByDay));
    }

    /**
     * 특정 월의 그룹 구성원 전체 일정 조회 (개인 + 그룹 일정 모두 포함)
     * 예: /calendar/group/8/schedules?year=2025&month=12
     */
    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<?> getGroupMemberSchedules(
            @PathVariable Long groupId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        var schedules = calendarGroupService.getMonthlySchedulesForGroupMembers(groupId, year, month);
        return ResponseEntity.ok(schedules);
    }
}