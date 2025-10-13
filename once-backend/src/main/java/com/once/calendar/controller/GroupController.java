package com.once.calendar.controller;

import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    // 그룹 관련 로직이 많아지면 별도의 GroupService를 만드는 것이 좋습니다.
    // 여기서는 CalendarService를 그대로 사용합니다.
    private final CalendarService calendarService;

    // 그룹원 Free/Busy 조회
    @GetMapping("/{groupId}/freebusy")
    public ResponseEntity<GroupFreeBusyResponse> getGroupFreeBusy(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        GroupFreeBusyResponse response = calendarService.getGroupFreeBusy(groupId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // 그룹 모임 불가 주 분석
    @GetMapping("/{groupId}/unavailable-weeks")
    public ResponseEntity<UnavailableWeeksResponse> getUnavailableWeeks(
            @PathVariable Long groupId,
            @RequestParam int year,
            @RequestParam int month) {

        UnavailableWeeksResponse response = calendarService.getUnavailableWeeks(groupId, year, month);
        return ResponseEntity.ok(response);
    }
}