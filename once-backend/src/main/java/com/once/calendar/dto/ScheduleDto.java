package com.once.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleDto {

    // request dtos

    @Getter
    @Setter
    public static class ScheduleCreateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Size(max = 200, message = "메모는 최대 200자까지 가능합니다.")
        private String memo;

        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private Long groupId; // optional
    }

    @Getter
    @Setter
    public static class ScheduleUpdateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Size(max = 200, message = "메모는 최대 200자까지 가능합니다.")
        private String memo;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }


    // response dtos

    public record GeneralResponse(String message) {}

    public record ScheduleCreateResponse(Long scheduleId, String message) {}

    public record ScheduleUpdateResponse(Long scheduleId, String message) {}

    public record MonthlyScheduleResponse(List<ScheduleInfo> schedules) {}

    public record DailyScheduleResponse(List<DailyScheduleInfo> schedules) {}

    public record ScheduleDetailResponse(
            Long scheduleId,
            String title,
            String memo,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String type,
            Long groupId,
            String groupName
    ) {}

    // 월별 조회시 사용될 일정 정보
    public record ScheduleInfo(
            Long scheduleId,
            String title,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String type,
            String color
    ) {}

    // 일별 조회시 사용될 일정 정보
    public record DailyScheduleInfo(
            Long scheduleId,
            String title,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {}

    // 그룹 관련 dtos
    public record GroupFreeBusyResponse(
            List<AvailableSlot> availableSlots,
            List<LocalDate> allMembersFreeDays
    ) {}

    public record AvailableSlot(LocalDateTime startDateTime, LocalDateTime endDateTime) {}

    public record UnavailableWeeksResponse(List<UnavailableWeek> unavailableWeeks) {}

    public record UnavailableWeek(int weekNumber, String reason) {}
}