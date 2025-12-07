/**
 * File: CalendarService.java
 * Description:
 *  - 개인/그룹 일정 생성·수정·삭제·조회 서비스
 *  - JWT 인증 기반 사용자 일정 처리
 *  - 그룹 구성원 일정 조회 포함
 */

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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final CalendarScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // JWT 인증된 유저 ID 확인
    private Long getUserIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증되지 않은 요청입니다.");
        }
        return ((CustomUserDetails) auth.getPrincipal()).getId();
    }

    // 일정 생성
    @Transactional
    public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request) {
        Long userId = getUserIdFromAuth();

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime(), DATE_TIME_FORMATTER);
        ScheduleType type = (request.getGroupId() == null) ? ScheduleType.PERSONAL : ScheduleType.GROUP;

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

        return new ScheduleCreateResponse(saved.getScheduleId(), "일정이 성공적으로 등록되었습니다.");
    }

    // 일정 수정
    @Transactional
    public ScheduleUpdateResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {

        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime(), DATE_TIME_FORMATTER);

        schedule.update(request.getTitle(), request.getMemo(), start, end);

        return new ScheduleUpdateResponse(schedule.getScheduleId(), "일정이 성공적으로 수정되었습니다.");
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        CalendarSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        scheduleRepository.delete(schedule);
    }

    // 월 일정 조회 (개인 + 그룹 일정)
    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {

        Long userId = getUserIdFromAuth();

        List<GroupMember> myGroups = groupMemberRepository.findByUserId(userId);
        List<Long> groupIds = myGroups.stream().map(g -> g.getGroup().getId()).toList();

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                groupIds.isEmpty()
                        ? scheduleRepository.findPersonalSchedulesOnly(userId, start, end)
                        : scheduleRepository.findPersonalAndGroupSchedules(userId, groupIds, start, end);

        List<ScheduleInfo> list = schedules.stream().map(s -> {
            User user = userRepository.findById(s.getUserId()).orElse(null);
            String userName = (user != null)
                    ? (user.getNickname() != null ? user.getNickname() : user.getUsername())
                    : "알 수 없음";

            return new ScheduleInfo(
                    s.getScheduleId(),
                    s.getTitle(),
                    s.getStartDateTime(),
                    s.getEndDateTime(),
                    s.getType().name(),
                    getScheduleColor(s.getType()),
                    s.getUserId(),
                    userName
            );
        }).toList();

        return new MonthlyScheduleResponse(list);
    }

    // 특정 날짜 일정 조회 (개인 + 그룹 일정)
    public DailyScheduleResponse getDailySchedules(LocalDate date) {

        Long userId = getUserIdFromAuth();

        List<GroupMember> myGroups = groupMemberRepository.findByUserId(userId);
        List<Long> groupIds = myGroups.stream().map(g -> g.getGroup().getId()).toList();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                groupIds.isEmpty()
                        ? scheduleRepository.findPersonalSchedulesOnly(userId, start, end)
                        : scheduleRepository.findPersonalAndGroupSchedules(userId, groupIds, start, end);

        List<DailyScheduleInfo> list = schedules.stream().map(s -> {
            User user = userRepository.findById(s.getUserId()).orElse(null);
            String userName = (user != null ? user.getNickname() : "알 수 없음");

            return new DailyScheduleInfo(
                    s.getScheduleId(),
                    s.getTitle(),
                    s.getStartDateTime(),
                    s.getEndDateTime(),
                    s.getUserId(),
                    userName
            );
        }).toList();

        return new DailyScheduleResponse(list);
    }

    // 일정 상세 조회
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

    // 그룹 멤버 개인 일정 조회 (그룹 캘린더)
    public MonthlyScheduleResponse getGroupMembersSchedules(Long groupId, int year, int month) {

        Long userId = getUserIdFromAuth();

        boolean isMember = groupMemberRepository
                .findByGroupIdAndUserId(groupId, userId)
                .isPresent();

        if (!isMember) throw new RuntimeException("그룹 멤버만 조회할 수 있습니다.");

        List<Long> memberUserIds = groupMemberRepository.findUserIdsByGroupId(groupId);
        if (memberUserIds.isEmpty()) {
            return new MonthlyScheduleResponse(List.of());
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                scheduleRepository.findPersonalSchedulesByUserIds(memberUserIds, start, end);

        List<ScheduleInfo> list = schedules.stream().map(s -> {
            User user = userRepository.findById(s.getUserId()).orElse(null);
            String userName =
                    (user != null)
                            ? (user.getNickname() != null ? user.getNickname() : user.getUsername())
                            : "알 수 없음";

            return new ScheduleInfo(
                    s.getScheduleId(),
                    s.getTitle(),
                    s.getStartDateTime(),
                    s.getEndDateTime(),
                    s.getType().name(),
                    getScheduleColor(s.getType()),
                    s.getUserId(),
                    userName
            );
        }).toList();

        return new MonthlyScheduleResponse(list);
    }

    // 일정 색상 설정
    private String getScheduleColor(ScheduleType type) {
        return type == ScheduleType.PERSONAL ? "#FFA726" : "#6799FF";
    }
}