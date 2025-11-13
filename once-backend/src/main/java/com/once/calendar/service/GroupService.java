package com.once.calendar.service;

import com.once.calendar.domain.Schedule;
import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ScheduleRepository scheduleRepository;
    // private final GroupMemberRepository groupMemberRepository; // 그룹 멤버 조회를 위함

    // 그룹원 free / busy 조회
    public GroupFreeBusyResponse getGroupFreeBusy(Long groupId, LocalDate startDate, LocalDate endDate) {

        // 1. 그룹에 속한 모든 멤버의 userId 목록 조회ㅣ
        // List<Long> memberUserIds = groupMemberRepository.findUserIdsByGroupId(groupId);
        List<Long> memberUserIds = List.of(1L, 2L, 3L); // 임시 데이터

        // 2. 해당 그룹의 그룹 일정 ID 목록 (여기서는 groupId 자체만 사용)
        List<Long> groupIds = List.of(groupId);

        // 3. 멤버들의 모든 일정(개인+그룹)을 기간 내에서 조회
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        List<Schedule> allSchedules = scheduleRepository.findAllSchedulesForGroupMembers(memberUserIds, groupIds, start, end);

        // 4. 조회된 일정을 바탕으로 Busy 시간대 목록(Time Blocks) 생성
        // todo Interval Merging 알고리즘 구현
        // - 모든 일정을 (start, end) 시간 블록으로 변환
        // - 겹치는 시간대를 병합하여 최종 'Busy' 시간대 목록 생성
        List<AvailableSlot> busySlots = mergeIntervals(allSchedules);

        // 5. Busy 시간대를 반전시켜 Free 시간대(availableSlots) 계산
        List<AvailableSlot> availableSlots = invertBusySlots(busySlots, start, end);

        // 6. 모든 멤버가 하루 종일 가능한 날짜(allMembersFreeDays) 계산
        List<LocalDate> freeDays = findFreeDays(busySlots, startDate, endDate);

        return new GroupFreeBusyResponse(availableSlots, freeDays);
    }

    // 그룹 모임 불가 주 분석
    public UnavailableWeeksResponse getUnavailableWeeks(Long groupId, int year, int month) {

        // 1. 그룹 멤버 ID 및 일정 조회 (getGroupFreeBusy와 유사)
        //

        // 2. 주차별로 일정 분석
        // todo "모임 불가"에 대한 비즈니스 로직 정의 필요
        // 예: 주간 일정 점유율 70% 이상, 휴가/외근 등 특정 키워드 일정 다수 포진

        List<UnavailableWeek> weeks = new ArrayList<>();

        // 임시
        // 2주차 분석
        // if (analyzeWeek(groupId, year, month, 2) > 0.7) {
        weeks.add(new UnavailableWeek(2, "주요 프로젝트 마감일"));
        // }
        // 4주차 분석
        // if (hasMultipleVacations(groupId, year, month, 4)) {
        weeks.add(new UnavailableWeek(4, "팀원 다수 휴가"));
        // }

        return new UnavailableWeeksResponse(weeks);
    }

    private List<AvailableSlot> mergeIntervals(List<Schedule> schedules) {
        // todo interval merging
        // 1. 스케줄을 시작 시간 기준으로 정렬
        // 2. 순회하면서 겹치는 시간 병합
        return new ArrayList<>();  // 임시
    }

    private List<AvailableSlot> invertBusySlots(List<AvailableSlot> busySlots, LocalDateTime periodStart, LocalDateTime periodEnd) {
        // todo 전체 기간(periodStart ~ periodEnd)에서 busySlots을 제외한 나머지 시간(free)을 계산
        return new ArrayList<>(); // 임시
    }

    private List<LocalDate> findFreeDays(List<AvailableSlot> busySlots, LocalDate startDate, LocalDate endDate) {
        // TODO: busySlots을 확인하여, 하루 종일(00:00~23:59) busy slot이 없는 날짜를 찾아 반환
        return new ArrayList<>(); // 임시
    }
}
