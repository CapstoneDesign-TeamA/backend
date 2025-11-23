package com.once.calendar.service;

import com.once.calendar.domain.CalendarSchedule;
import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.CalendarScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("calendarGroupService")  // 임시
@RequiredArgsConstructor
public class GroupService {

    private final CalendarScheduleRepository scheduleRepository;
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
        List<CalendarSchedule> allSchedules = scheduleRepository.findAllSchedulesForGroupMembers(memberUserIds, groupIds, start, end);

        // 4. 조회된 일정을 바탕으로 Busy 시간대 목록(Time Blocks) 생성
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

        // 조회하려는 달(Month)의 시작일과 마지막일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDate = yearMonth.atDay(1);
        LocalDate lastDate = yearMonth.atEndOfMonth();

        // 해당 기간의 그룹원들 모든 일정 가져오기
        // (groupMemberRepository로 멤버 ID를 가져와야 함)
        List<Long> memberUserIds = List.of(1L, 2L, 3L); // 임시 멤버 ID
        List<Long> groupIds = List.of(groupId);

        LocalDateTime startDateTime = firstDate.atStartOfDay();
        LocalDateTime endDateTime = lastDate.atTime(23, 59, 59);

        List<CalendarSchedule> monthlySchedules = scheduleRepository.findAllSchedulesForGroupMembers(
                memberUserIds, groupIds, startDateTime, endDateTime
        );

        // 주차별로 일정을 분류하고 분석
        List<UnavailableWeek> unavailableWeeks = new ArrayList<>();

        // 달의 첫 주부터 마지막 주까지 반복
        LocalDate current = firstDate;
        // 한국 기준 주차 계산기
        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        while (!current.isAfter(lastDate)) {
            // 현재 날짜가 몇 주차인지 계산
            int weekNum = current.get(weekFields.weekOfMonth());

            // 이번 주의 시작일(일)과 종료일(토) 계산 (월 범위를 벗어나지 않게)
            LocalDate weekStart = current;
            LocalDate weekEnd = current.plusDays(6);
            if (weekEnd.isAfter(lastDate)) weekEnd = lastDate;

            // 이번 주에 일정이 얼마나 꽉 찼는지 분석
            String reason = analyzeWeekBusyness(monthlySchedules, weekStart, weekEnd);

            // 이유가 반환되었다면(= 바쁜 주간이라면) 결과 목록에 추가
            if (reason != null) {
                // 이미 같은 주차가 리스트에 있다면 중복 추가 방지
                int finalWeekNum = weekNum;
                boolean exists = unavailableWeeks.stream().anyMatch(w -> w.weekNumber() == finalWeekNum);
                if (!exists) {
                    unavailableWeeks.add(new UnavailableWeek(weekNum, reason));
                }
            }

            // 다음 주 시작일로 점프
            current = current.plusWeeks(1);
        }

        return new UnavailableWeeksResponse(unavailableWeeks);
    }

    // 특정 주간이 바쁜지 판단하는 기준
    private String analyzeWeekBusyness(List<CalendarSchedule> schedules, LocalDate weekStart, LocalDate weekEnd) {

        LocalDateTime startDt = weekStart.atStartOfDay();
        LocalDateTime endDt = weekEnd.atTime(23, 59, 59);

        long totalBusyHours = 0;
        int scheduleCount = 0;

        for (CalendarSchedule s : schedules) {
            // 이 일정이 이번 주 범위 안에 포함되는지 확인 (조금이라도 걸치면 포함)
            if (s.getStartDateTime().isBefore(endDt) && s.getEndDateTime().isAfter(startDt)) {

                // 일정이 겹치는 시간만큼 계산 (분 단위)
                LocalDateTime effectiveStart = s.getStartDateTime().isBefore(startDt) ? startDt : s.getStartDateTime();
                LocalDateTime effectiveEnd = s.getEndDateTime().isAfter(endDt) ? endDt : s.getEndDateTime();

                long minutes = ChronoUnit.MINUTES.between(effectiveStart, effectiveEnd);
                totalBusyHours += minutes;
                scheduleCount++;
            }
        }

        // 시간을 시간 단위로 변환
        totalBusyHours = totalBusyHours / 60;

        // 이번 주에 그룹원 전체 일정 합계가 20시간이 넘으면 일정 과다
        if (totalBusyHours > 20) {
            return "그룹원 전체 일정이 많아요. (" + totalBusyHours + "시간)";
        }

        // 이번 주에 일정이 8개 이상 잡혀있으면 이벤트 다수
        if (scheduleCount > 8) {
            return "주요 이벤트 다수 예정이에요 (" + scheduleCount + "건)";
        }

        // 안 바쁨 (null 반환)
        return null;
    }

    // 여러 사람의 섞인 일정을 받아서 겹치는 시간대를 모두 합쳐 'Busy' 구간을 만듦
    private List<AvailableSlot> mergeIntervals(List<CalendarSchedule> schedules) {
        if (schedules.isEmpty()) {
            return new ArrayList<>();
        }

        // 시작 시간 기준으로 오름차순 정렬
        schedules.sort(Comparator.comparing(CalendarSchedule::getStartDateTime));

        List<AvailableSlot> merged = new ArrayList<>();

        // 첫 번째 일정을 기준으로 시작
        CalendarSchedule first = schedules.get(0);
        LocalDateTime currentStart = first.getStartDateTime();
        LocalDateTime currentEnd = first.getEndDateTime();

        for (int i = 1; i < schedules.size(); i++) {
            CalendarSchedule next = schedules.get(i);

            // 현재 일정의 끝(currentEnd)보다 다음 일정의 시작(next.Start)이 빠르거나 같다면 겹침
            if (!next.getStartDateTime().isAfter(currentEnd)) {
                // 겹치면 끝나는 시간을 둘 중 더 늦은 것으로 연장
                if (next.getEndDateTime().isAfter(currentEnd)) {
                    currentEnd = next.getEndDateTime();
                }
            } else {
                // 안 겹치면 지금까지 뭉친 구간을 저장하고 새로운 구간 시작
                merged.add(new AvailableSlot(currentStart, currentEnd));
                currentStart = next.getStartDateTime();
                currentEnd = next.getEndDateTime();
            }
        }
        // 마지막 남은 구간 저장
        merged.add(new AvailableSlot(currentStart, currentEnd));

        return merged;
    }

     // 전체 조회 기간(periodStart ~ periodEnd)에서 'Busy' 시간을 빼고 남는 시간을 'Free'로 반환
    private List<AvailableSlot> invertBusySlots(List<AvailableSlot> busySlots, LocalDateTime periodStart, LocalDateTime periodEnd) {
        List<AvailableSlot> freeSlots = new ArrayList<>();

        // 현재 포인터를 조회 시작 시간으로 둠
        LocalDateTime currentPointer = periodStart;

        for (AvailableSlot busy : busySlots) {
            // Busy 시작 시간이 현재 포인터보다 뒤에 있으면 그 사이가 비었다는 뜻 (Free)
            if (busy.startDateTime().isAfter(currentPointer)) {
                freeSlots.add(new AvailableSlot(currentPointer, busy.startDateTime()));
            }

            // 현재 포인터를 Busy 끝나는 시간으로 점프 (이미 바쁜 시간은 건너뜀)
            // (이전 Busy보다 더 늦게 끝나는 경우에만 업데이트)
            if (busy.endDateTime().isAfter(currentPointer)) {
                currentPointer = busy.endDateTime();
            }
        }

        // 모든 Busy를 다 돌고 나서 아직 조회 끝나는 시간까지 남았다면 마지막 남은 시간도 Free
        if (currentPointer.isBefore(periodEnd)) {
            freeSlots.add(new AvailableSlot(currentPointer, periodEnd));
        }

        return freeSlots;
    }

     // Busy' 시간대가 하나도 겹치지 않는 날짜(하루 종일 Free)를 찾음
    private List<LocalDate> findFreeDays(List<AvailableSlot> busySlots, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> wholeFreeDays = new ArrayList<>();

        // 조회 기간의 모든 날짜를 하루씩 돌면서 검사
        LocalDate targetDate = startDate;
        while (!targetDate.isAfter(endDate)) {

            // 검사하려는 날짜의 시작(00:00)과 끝(23:59)
            LocalDateTime dayStart = targetDate.atStartOfDay();
            LocalDateTime dayEnd = targetDate.atTime(23, 59, 59);

            boolean isBusy = false;

            // Busy 슬롯 중 하나라도 이 날짜와 겹치는지 확인
            for (AvailableSlot busy : busySlots) {
                // (Busy 시작 < 오늘 끝) AND (Busy 끝 > 오늘 시작) 이면 겹침
                if (busy.startDateTime().isBefore(dayEnd) && busy.endDateTime().isAfter(dayStart)) {
                    isBusy = true;
                    break;
                }
            }

            // 하나도 안 겹치면 자유로운 날
            if (!isBusy) {
                wholeFreeDays.add(targetDate);
            }

            // 다음 날짜로 이동
            targetDate = targetDate.plusDays(1);
        }
        return wholeFreeDays;
    }
}