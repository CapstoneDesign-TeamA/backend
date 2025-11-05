package com.once.calendar.service;

import com.once.calendar.domain.Schedule;
import com.once.calendar.domain.ScheduleType;
import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final ScheduleRepository scheduleRepository;
    // private final GroupMemberService groupMemberService; // 내가 속한 그룹 ID 목록을 가져오기 위함
    // private final GroupRepository groupRepository; // 그룹 이름 등을 가져오기 위함

    // 임시로 인증된 사용자 ID 반환
    private Long getUserIdFromAuth() {
        // todo 실제 사용자 id 가져오기
        return 1L;
    }

    // 임시로 사용자가 속한 그룹 id 목록 반환
    private List<Long> getMyGroupIds(Long userId) {
        // todo groupMemberService 등을 통해 실제 사용자가 속한 그룹 id 목록 조회
        return List.of(101L, 102L);
    }

    @Transactional
    public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request) {
        Long userId = getUserIdFromAuth();

        ScheduleType type = (request.getGroupId() == null) ? ScheduleType.PERSONAL : ScheduleType.GROUP;

        Schedule schedule = Schedule.builder()
                .userId(userId)
                .groupId(request.getGroupId())
                .title(request.getTitle())
                .memo(request.getMemo())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .type(type)
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return new ScheduleCreateResponse(savedSchedule.getScheduleId(), "일정이 성공적으로 등록되었습니다.");
    }

    @Transactional
    public ScheduleUpdateResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Long userId = getUserIdFromAuth();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // todo 일정 수정 권한 확인
        /*
        if (!schedule.getUserId().equals(userId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }
        */

        schedule.update(request.getTitle(), request.getMemo(), request.getStartDateTime(), request.getEndDateTime());

        return new ScheduleUpdateResponse(schedule.getScheduleId(), "일정이 성공적으로 수정되었습니다.");
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Long userId = getUserIdFromAuth();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // todo 일정 삭제 권한 확인
        /*
        if (!schedule.getUserId().equals(userId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        */

        scheduleRepository.delete(schedule);
    }

    // 월 단위 일정 조회
    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {
        Long userId = getUserIdFromAuth();
        List<Long> myGroupIds = getMyGroupIds(userId);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository.findPersonalAndGroupSchedules(userId, myGroupIds, start, end);

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

        List<Schedule> schedules = scheduleRepository.findPersonalAndGroupSchedules(userId, myGroupIds, start, end);

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

    public ScheduleDetailResponse getScheduleDetails(Long scheduleId) {
        Long userId = getUserIdFromAuth();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // todo 일정 조회 권한 확인 (내가 속한 그룹의 일정이거나 내 일정인지)

        String groupName = null;
        if (schedule.getType() == ScheduleType.GROUP && schedule.getGroupId() != null) {
            // todo groupRepository.findById(schedule.getGroupId()).map(Group::getName).orElse(null);
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

    // 개인 / 그룹 일정 구분
    private String getScheduleColor(ScheduleType type) {
        return (type == ScheduleType.PERSONAL) ? "#000000" : "#6799FF";
    }
}