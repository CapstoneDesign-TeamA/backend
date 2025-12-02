package com.once.calendar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleDto {

    // ===== 공통 응답 =====
    public static class GeneralResponse {
        private String message;

        public GeneralResponse() {}

        public GeneralResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // =========================
    // 1) 일정 생성 DTO
    // =========================
    public static class ScheduleCreateRequest {

        private Long groupId;
        private String title;
        private String memo;

        // 문자열로 받는다. "2025-11-20T19:00:00"
        private String startDateTime;
        private String endDateTime;

        public ScheduleCreateRequest() {}

        public Long getGroupId() { return groupId; }
        public void setGroupId(Long groupId) { this.groupId = groupId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }

        public String getStartDateTime() { return startDateTime; }
        public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }

        public String getEndDateTime() { return endDateTime; }
        public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }
    }

    public static class ScheduleCreateResponse {
        private Long scheduleId;
        private String message;

        public ScheduleCreateResponse() {}

        public ScheduleCreateResponse(Long scheduleId, String message) {
            this.scheduleId = scheduleId;
            this.message = message;
        }

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // =========================
    // 2) 일정 수정 DTO
    // =========================
    public static class ScheduleUpdateRequest {

        private String title;
        private String memo;
        private String startDateTime;
        private String endDateTime;

        public ScheduleUpdateRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }

        public String getStartDateTime() { return startDateTime; }
        public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }

        public String getEndDateTime() { return endDateTime; }
        public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }
    }

    public static class ScheduleUpdateResponse {
        private Long scheduleId;
        private String message;

        public ScheduleUpdateResponse() {}

        public ScheduleUpdateResponse(Long scheduleId, String message) {
            this.scheduleId = scheduleId;
            this.message = message;
        }

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // =========================
    // 3) 월 조회 DTO
    // =========================
    public static class ScheduleInfo {
        private Long scheduleId;
        private String title;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String type;
        private String color;

        // 추가된 필드
        private Long userId;
        private String userName;

        public ScheduleInfo() {}

        public ScheduleInfo(
                Long scheduleId,
                String title,
                LocalDateTime startDateTime,
                LocalDateTime endDateTime,
                String type,
                String color,
                Long userId,
                String userName
        ) {
            this.scheduleId = scheduleId;
            this.title = title;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.type = type;
            this.color = color;
            this.userId = userId;
            this.userName = userName;
        }

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public LocalDateTime getStartDateTime() { return startDateTime; }
        public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

        public LocalDateTime getEndDateTime() { return endDateTime; }
        public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    public static class MonthlyScheduleResponse {
        private List<ScheduleInfo> schedules;

        public MonthlyScheduleResponse() {}

        public MonthlyScheduleResponse(List<ScheduleInfo> schedules) {
            this.schedules = schedules;
        }

        public List<ScheduleInfo> getSchedules() { return schedules; }
        public void setSchedules(List<ScheduleInfo> schedules) { this.schedules = schedules; }
    }

    // =========================
    // 4) 일 조회 DTO
    // =========================
    public static class DailyScheduleInfo {

        private Long scheduleId;
        private String title;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;


        private Long userId;
        private String userName;

        public DailyScheduleInfo() {}

        public DailyScheduleInfo(
                Long scheduleId,
                String title,
                LocalDateTime startDateTime,
                LocalDateTime endDateTime,
                Long userId,
                String userName
        ) {
            this.scheduleId = scheduleId;
            this.title = title;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.userId = userId;
            this.userName = userName;
        }

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public LocalDateTime getStartDateTime() { return startDateTime; }
        public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

        public LocalDateTime getEndDateTime() { return endDateTime; }
        public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    public static class DailyScheduleResponse {
        private List<DailyScheduleInfo> schedules;

        public DailyScheduleResponse() {}

        public DailyScheduleResponse(List<DailyScheduleInfo> schedules) {
            this.schedules = schedules;
        }

        public List<DailyScheduleInfo> getSchedules() { return schedules; }
        public void setSchedules(List<DailyScheduleInfo> schedules) { this.schedules = schedules; }
    }

    // =========================
    // 5) 상세 조회 DTO
    // =========================
    public static class ScheduleDetailResponse {
        private Long scheduleId;
        private String title;
        private String memo;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String type;
        private Long groupId;
        private String groupName;

        public ScheduleDetailResponse() {}

        public ScheduleDetailResponse(Long scheduleId, String title, String memo,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      String type, Long groupId, String groupName) {
            this.scheduleId = scheduleId;
            this.title = title;
            this.memo = memo;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.type = type;
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }

        public LocalDateTime getStartDateTime() { return startDateTime; }
        public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

        public LocalDateTime getEndDateTime() { return endDateTime; }
        public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Long getGroupId() { return groupId; }
        public void setGroupId(Long groupId) { this.groupId = groupId; }

        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
    }

    // =========================
    // 6) 그룹 Free / Busy DTO
    // =========================
    public static class AvailableSlot {
        private LocalDateTime start;
        private LocalDateTime end;

        public AvailableSlot() {}

        public AvailableSlot(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() { return start; }
        public void setStart(LocalDateTime start) { this.start = start; }

        public LocalDateTime getEnd() { return end; }
        public void setEnd(LocalDateTime end) { this.end = end; }
    }

    public static class GroupFreeBusyResponse {
        private List<AvailableSlot> availableSlots;
        private List<LocalDate> allMembersFreeDays;

        public GroupFreeBusyResponse() {}

        public GroupFreeBusyResponse(List<AvailableSlot> availableSlots,
                                     List<LocalDate> allMembersFreeDays) {
            this.availableSlots = availableSlots;
            this.allMembersFreeDays = allMembersFreeDays;
        }

        public List<AvailableSlot> getAvailableSlots() { return availableSlots; }
        public void setAvailableSlots(List<AvailableSlot> availableSlots) { this.availableSlots = availableSlots; }

        public List<LocalDate> getAllMembersFreeDays() { return allMembersFreeDays; }
        public void setAllMembersFreeDays(List<LocalDate> allMembersFreeDays) { this.allMembersFreeDays = allMembersFreeDays; }
    }

    // =========================
    // 7) 모임 불가 주 DTO
    // =========================
    public static class UnavailableWeek {
        private int weekOfMonth;
        private String reason;

        public UnavailableWeek() {}

        public UnavailableWeek(int weekOfMonth, String reason) {
            this.weekOfMonth = weekOfMonth;
            this.reason = reason;
        }

        public int getWeekOfMonth() { return weekOfMonth; }
        public void setWeekOfMonth(int weekOfMonth) { this.weekOfMonth = weekOfMonth; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class UnavailableWeeksResponse {
        private List<UnavailableWeek> weeks;

        public UnavailableWeeksResponse() {}

        public UnavailableWeeksResponse(List<UnavailableWeek> weeks) {
            this.weeks = weeks;
        }

        public List<UnavailableWeek> getWeeks() { return weeks; }
        public void setWeeks(List<UnavailableWeek> weeks) { this.weeks = weeks; }
    }
}