package com.once.calendar.controller;

import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class CalendarGroupController {

    private final GroupService groupService;

    // 그룹원 Free/Busy 조회
    @GetMapping("/{groupId}/freebusy")
    public ResponseEntity<GroupFreeBusyResponse> getGroupFreeBusy(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        GroupFreeBusyResponse response = groupService.getGroupFreeBusy(groupId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // 그룹 모임 불가 주 분석
    @GetMapping("/{groupId}/unavailable-weeks")
    public ResponseEntity<UnavailableWeeksResponse> getUnavailableWeeks(
            @PathVariable Long groupId,
            @RequestParam int year,
            @RequestParam int month) {

        UnavailableWeeksResponse response = groupService.getUnavailableWeeks(groupId, year, month);
        return ResponseEntity.ok(response);
    }
}