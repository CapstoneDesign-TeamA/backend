/**
 * File: CalendarGroupController.java
 * Description:
 *  - 그룹 구성원 일정 기반 busy-count 조회
 *  - 그룹 전체 구성원의 월별 일정 조회 API
 */

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

    // 날짜 범위별 그룹 멤버 busy-count 조회
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

    // 그룹 멤버 전체 월별 일정 조회 (개인 + 그룹 일정 포함)
    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<?> getGroupMemberSchedules(
            @PathVariable Long groupId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        var schedules =
                calendarGroupService.getMonthlySchedulesForGroupMembers(groupId, year, month);

        return ResponseEntity.ok(schedules);
    }
}