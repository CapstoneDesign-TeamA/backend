package com.once.calendar.service;

import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션
public class CalendarService {

    private final ScheduleRepository scheduleRepository;

    @Transactional // 쓰기 작업에는 별도 어노테이션 추가
    public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request) {
        // TODO: request DTO를 Schedule 엔티티로 변환하여 저장하는 로직
        // Schedule schedule = new Schedule(...);
        // Schedule savedSchedule = scheduleRepository.save(schedule);
        System.out.println("일정 등록 로직 처리");
        Long newScheduleId = 1L; // 임시 ID
        return new ScheduleCreateResponse(newScheduleId, "일정이 성공적으로 등록되었습니다.");
    }

    @Transactional
    public ScheduleUpdateResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        // TODO: scheduleId로 기존 일정을 찾고, request 내용으로 업데이트하는 로직
        // Schedule schedule = scheduleRepository.findById(scheduleId)
        //      .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        // schedule.update(...);
        System.out.println(scheduleId + "번 일정 수정 로직 처리");
        return new ScheduleUpdateResponse(scheduleId, "일정이 성공적으로 수정되었습니다.");
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        // TODO: scheduleId로 일정을 찾아 삭제하는 로직
        // scheduleRepository.deleteById(scheduleId);
        System.out.println(scheduleId + "번 일정 삭제 로직 처리");
    }

    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {
        // TODO: year, month에 해당하는 일정 목록을 DB에서 조회하는 로직
        // YearMonth yearMonth = YearMonth.of(year, month);
        // LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        // LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        // List<Schedule> schedules = scheduleRepository.findAllByStartDateTimeBetween(start, end);
        System.out.println(year + "년 " + month + "월 일정 조회 로직 처리");

        // 임시 데이터
        List<ScheduleInfo> scheduleInfos = List.of(
                new ScheduleInfo(1L, "팀 회의", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "GROUP", "#4285F4")
        );
        return new MonthlyScheduleResponse(scheduleInfos);
    }

    public DailyScheduleResponse getDailySchedules(LocalDate date) {
        // TODO: date에 해당하는 일정 목록을 DB에서 조회하는 로직
        System.out.println(date + " 날짜 일정 조회 로직 처리");

        // 임시 데이터
        List<DailyScheduleInfo> scheduleInfos = List.of(
                new DailyScheduleInfo(1L, "팀 회의", date.atTime(10, 0), date.atTime(11, 0))
        );
        return new DailyScheduleResponse(scheduleInfos);
    }

    public ScheduleDetailResponse getScheduleDetails(Long scheduleId) {
        // TODO: scheduleId로 일정 상세 정보를 조회하는 로직 (연관된 그룹 정보 포함)
        System.out.println(scheduleId + "번 일정 상세 조회 로직 처리");

        // 임시 데이터
        return new ScheduleDetailResponse(
                scheduleId, "팀 회의", "주간 업무 보고",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                "GROUP", 101L, "개발팀"
        );
    }

    // --- 그룹 관련 서비스 로직 ---
    // 별도의 GroupService를 만들어 분리하는 것이 더 좋습니다.

    public GroupFreeBusyResponse getGroupFreeBusy(Long groupId, LocalDate startDate, LocalDate endDate) {
        // TODO: groupId에 속한 멤버들의 일정을 startDate와 endDate 사이에서 조회하여
        // 모두가 비어있는 시간(availableSlots)과 날짜(allMembersFreeDays)를 계산하는 로직
        System.out.println(groupId + "번 그룹 Free/Busy 조회 로직 처리");

        // 임시 데이터
        List<AvailableSlot> slots = List.of(new AvailableSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
        List<LocalDate> freeDays = List.of(LocalDate.now().plusDays(3));
        return new GroupFreeBusyResponse(slots, freeDays);
    }

    public UnavailableWeeksResponse getUnavailableWeeks(Long groupId, int year, int month) {
        // TODO: 해당 월에 그룹원들의 일정을 분석하여 모임이 불가한 주와 사유를 반환하는 로직
        System.out.println(groupId + "번 그룹 모임 불가 주 분석 로직 처리");

        // 임시 데이터
        List<UnavailableWeek> weeks = List.of(new UnavailableWeek(2, "팀원 다수 휴가"));
        return new UnavailableWeeksResponse(weeks);
    }
}