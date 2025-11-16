// src/main/java/com/once/calendar/service/CalendarService.java
package com.once.calendar.service;

import com.once.calendar.domain.CalendarSchedule;
import com.once.calendar.domain.ScheduleType;
import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.CalendarScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    // 클래스 안에 공통 포맷터 하나 정의
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final CalendarScheduleRepository scheduleRepository;

    // 임시: 인증된 사용자 ID
    private Long getUserIdFromAuth() {
        return 1L;
    }

    // 임시: 사용자가 속한 그룹 ID 리스트
    private List<Long> getMyGroupIds(Long userId) {
        return List.of(101L, 102L);
    }

    // 일정 생성
    @Transactional
    public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request) {
        Long userId = getUserIdFromAuth();

        if (request == null) {
            throw new IllegalArgumentException("요청 본문이 비어 있습니다.");
        }
        if (request.getStartDateTime() == null || request.getEndDateTime() == null) {
            throw new IllegalArgumentException("startDateTime / endDateTime 은 필수 값입니다.");
        }

        // 문자열 → LocalDateTime 변환
        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime(), DATE_TIME_FORMATTER);

        System.out.println("[DEBUG] parsed start=" + start + ", end=" + end);

        ScheduleType type =
                (request.getGroupId() == null) ? ScheduleType.PERSONAL : ScheduleType.GROUP;

        CalendarSchedule schedule = CalendarSchedule.builder()
                .userId(userId)
                .groupId(request.getGroupId())
                .title(request.getTitle())
                .memo(request.getMemo())
                .startDateTime(start)
                .endDateTime(end)
                .type(type)
                .build();

        CalendarSchedule saved = scheduleRepository.save(schedule);

        return new ScheduleCreateResponse(
                saved.getScheduleId(),
                "일정이 성공적으로 등록되었습니다."
        );
    }

    // 일정 수정
    @Transactional
    public ScheduleUpdateResponse updateSchedule(Long scheduleId,
                                                 ScheduleUpdateRequest request) {
        Long userId = getUserIdFromAuth();

        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        if (request == null) {
            throw new IllegalArgumentException("요청 본문이 비어 있습니다.");
        }
        if (request.getStartDateTime() == null || request.getEndDateTime() == null) {
            throw new IllegalArgumentException("startDateTime / endDateTime 은 필수 값입니다.");
        }

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime(), DATE_TIME_FORMATTER);

        schedule.update(
                request.getTitle(),
                request.getMemo(),
                start,
                end
        );

        return new ScheduleUpdateResponse(
                schedule.getScheduleId(),
                "일정이 성공적으로 수정되었습니다."
        );
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Long userId = getUserIdFromAuth();

        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // todo: 권한 체크

        scheduleRepository.delete(schedule);
    }

    // 월 단위 일정 조회
    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {
        Long userId = getUserIdFromAuth();
        List<Long> myGroupIds = getMyGroupIds(userId);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                scheduleRepository.findPersonalAndGroupSchedules(userId, myGroupIds, start, end);

        List<ScheduleInfo> scheduleInfos = schedules.stream()
                .map(s -> new ScheduleInfo(
                        s.getScheduleId(),
                        s.getTitle(),
                        s.getStartDateTime(),
                        s.getEndDateTime(),
                        s.getType().name(),
                        getScheduleColor(s.getType())
                ))
                .toList();

        return new MonthlyScheduleResponse(scheduleInfos);
    }

    // 특정 날짜 일정 목록 조회
    public DailyScheduleResponse getDailySchedules(LocalDate date) {
        Long userId = getUserIdFromAuth();
        List<Long> myGroupIds = getMyGroupIds(userId);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                scheduleRepository.findPersonalAndGroupSchedules(userId, myGroupIds, start, end);

        List<DailyScheduleInfo> scheduleInfos = schedules.stream()
                .map(s -> new DailyScheduleInfo(
                        s.getScheduleId(),
                        s.getTitle(),
                        s.getStartDateTime(),
                        s.getEndDateTime()
                ))
                .toList();

        return new DailyScheduleResponse(scheduleInfos);
    }

    // 일정 상세 조회
    public ScheduleDetailResponse getScheduleDetails(Long scheduleId) {
        Long userId = getUserIdFromAuth();

        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        String groupName = null;
        if (schedule.getType() == ScheduleType.GROUP && schedule.getGroupId() != null) {
            groupName = "a조"; // 임시
        }

        return new ScheduleDetailResponse(
                schedule.getScheduleId(),
                schedule.getTitle(),
                schedule.getMemo(),
                schedule.getStartDateTime(),
                schedule.getEndDateTime(),
                schedule.getType().name(),
                schedule.getGroupId(),
                groupName
        );
    }

    // 개인 / 그룹 일정 색상 구분
    private String getScheduleColor(ScheduleType type) {
        return (type == ScheduleType.PERSONAL) ? "#000000" : "#6799FF";
    }
}