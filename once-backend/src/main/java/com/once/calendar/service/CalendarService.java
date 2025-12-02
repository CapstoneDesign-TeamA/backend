package com.once.calendar.service;

import com.once.auth.domain.CustomUserDetails;
import com.once.calendar.domain.CalendarSchedule;
import com.once.calendar.domain.ScheduleType;
import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.CalendarScheduleRepository;
import com.once.group.domain.GroupMember;
import com.once.group.repository.GroupMemberRepository;
import com.once.user.domain.User;
import com.once.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final CalendarScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * JWT 인증된 유저 ID 추출
     */
    private Long getUserIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증되지 않은 요청입니다.");
        }
        return ((CustomUserDetails) auth.getPrincipal()).getId();
    }

    // ==========================================
    // 일정 생성
    // ==========================================
    @Transactional
    public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request) {
        Long userId = getUserIdFromAuth();

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime(), DATE_TIME_FORMATTER);

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

    // ==========================================
    // 일정 수정
    // ==========================================
    @Transactional
    public ScheduleUpdateResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {

        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime(), DATE_TIME_FORMATTER);

        schedule.update(request.getTitle(), request.getMemo(), start, end);

        return new ScheduleUpdateResponse(
                schedule.getScheduleId(),
                "일정이 성공적으로 수정되었습니다."
        );
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        scheduleRepository.delete(schedule);
    }

    // ==========================================
    // 월 일정 조회 (그룹 멤버 전체 일정 포함)
    // ==========================================
    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {

        Long userId = getUserIdFromAuth();

        // 유저가 속한 그룹 ID 전부 가져오기
        List<GroupMember> myGroups = groupMemberRepository.findByUserId(userId);
        List<Long> groupIds = myGroups.stream()
                .map(g -> g.getGroup().getId())
                .toList();

        if (groupIds.isEmpty()) {
            return new MonthlyScheduleResponse(List.of());
        }

        // 이 그룹들에 속한 전체 멤버들의 userId 가져오기
        List<Long> memberUserIds = groupIds.stream()
                .flatMap(gid -> groupMemberRepository.findUserIdsByGroupId(gid).stream())
                .distinct()
                .collect(Collectors.toList());

        // 날짜 범위
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        // 일정 조회
        List<CalendarSchedule> schedules =
                scheduleRepository.findAllSchedulesForGroupMembers(
                        memberUserIds, groupIds, start, end
                );

        List<ScheduleInfo> list = schedules.stream().map(s -> {
            User user = userRepository.findById(s.getUserId()).orElse(null);

            return new ScheduleInfo(
                    s.getScheduleId(),
                    s.getTitle(),
                    s.getStartDateTime(),
                    s.getEndDateTime(),
                    s.getType().name(),
                    getScheduleColor(s.getType()),
                    s.getUserId(),
                    user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "알 수 없음"
            );
        }).toList();

        return new MonthlyScheduleResponse(list);
    }

    // ==========================================
    // 특정 날짜 일정 조회
    // ==========================================
    public DailyScheduleResponse getDailySchedules(LocalDate date) {

        Long userId = getUserIdFromAuth();

        List<GroupMember> myGroups = groupMemberRepository.findByUserId(userId);
        List<Long> groupIds = myGroups.stream()
                .map(g -> g.getGroup().getId())
                .toList();

        if (groupIds.isEmpty()) {
            return new DailyScheduleResponse(List.of());
        }

        List<Long> memberUserIds = groupIds.stream()
                .flatMap(gid -> groupMemberRepository.findUserIdsByGroupId(gid).stream())
                .distinct()
                .collect(Collectors.toList());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                scheduleRepository.findAllSchedulesForGroupMembers(
                        memberUserIds, groupIds, start, end
                );

        List<DailyScheduleInfo> list = schedules.stream().map(s -> {
            User user = userRepository.findById(s.getUserId()).orElse(null);

            return new DailyScheduleInfo(
                    s.getScheduleId(),
                    s.getTitle(),
                    s.getStartDateTime(),
                    s.getEndDateTime(),
                    s.getUserId(),
                    user != null ? user.getNickname() : "알 수 없음"
            );
        }).toList();

        return new DailyScheduleResponse(list);
    }

    // ==========================================
    // 일정 상세 조회
    // ==========================================
    public ScheduleDetailResponse getScheduleDetails(Long scheduleId) {
        CalendarSchedule s = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        User user = userRepository.findById(s.getUserId()).orElse(null);

        return new ScheduleDetailResponse(
                s.getScheduleId(),
                s.getTitle(),
                s.getMemo(),
                s.getStartDateTime(),
                s.getEndDateTime(),
                s.getType().name(),
                s.getGroupId(),
                user != null ? user.getUsername() : null
        );
    }

    private String getScheduleColor(ScheduleType type) {
        return type == ScheduleType.PERSONAL ? "#FFA726" : "#6799FF";
    }
}