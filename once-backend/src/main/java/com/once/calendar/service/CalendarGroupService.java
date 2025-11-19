package com.once.calendar.service;

import com.once.calendar.domain.CalendarSchedule;
import com.once.calendar.dto.ScheduleDto.*;
import com.once.calendar.repository.CalendarScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarGroupService {

    private final CalendarScheduleRepository scheduleRepository;
    // private final GroupMemberRepository groupMemberRepository; // 그룹 멤버 조회

    // 그룹원 Free / Busy 조회
    public GroupFreeBusyResponse getGroupFreeBusy(Long groupId,
                                                  LocalDate startDate,
                                                  LocalDate endDate) {

        // 1. 그룹에 속한 모든 멤버의 userId 목록 조회 (임시 데이터)
        // List<Long> memberUserIds = groupMemberRepository.findUserIdsByGroupId(groupId);
        List<Long> memberUserIds = List.of(1L, 2L, 3L); // 임시

        // 2. 해당 그룹의 groupId 목록
        List<Long> groupIds = List.of(groupId);

        // 3. 기간 내 모든 일정 조회 (개인 + 그룹)
        LocalDateTime periodStart = startDate.atStartOfDay();
        LocalDateTime periodEnd = endDate.atTime(23, 59, 59);

        List<CalendarSchedule> allSchedules =
                scheduleRepository.findAllSchedulesForGroupMembers(
                        memberUserIds, groupIds, periodStart, periodEnd);

        // 4. Busy 구간 병합
        List<AvailableSlot> busySlots = mergeIntervals(allSchedules);

        // 5. 전체 기간에서 Busy 를 제외한 Free 구간 계산
        List<AvailableSlot> freeSlots = invertBusySlots(busySlots, periodStart, periodEnd);

        // 6. 하루 종일(00~24시) 모두 Free 인 날짜 계산
        List<LocalDate> allMembersFreeDays =
                findFreeDays(busySlots, startDate, endDate);

        return new GroupFreeBusyResponse(freeSlots, allMembersFreeDays);
    }

    // 그룹 모임 불가 주 분석 (임시 로직)
    public UnavailableWeeksResponse getUnavailableWeeks(Long groupId,
                                                        int year,
                                                        int month) {

        List<UnavailableWeek> weeks = new ArrayList<>();

        // 실제 로직: 주차별로 일정 밀집도나 휴가 일정 등을 분석
        // 여기서는 예시 데이터만 넣어둠
        weeks.add(new UnavailableWeek(2, "주요 프로젝트 마감일"));
        weeks.add(new UnavailableWeek(4, "팀원 다수 휴가"));

        return new UnavailableWeeksResponse(weeks);
    }

    // ===== Interval Merge / Invert / Free-day 계산 =====

    // 캘린더 일정들을 Busy Slot 으로 변환 후 병합
    private List<AvailableSlot> mergeIntervals(List<CalendarSchedule> schedules) {
        List<AvailableSlot> slots = new ArrayList<>();

        for (CalendarSchedule s : schedules) {
            slots.add(new AvailableSlot(
                    s.getStartDateTime(),
                    s.getEndDateTime()
            ));
        }

        if (slots.isEmpty()) {
            return slots;
        }

        // 시작 시간 기준 정렬
        slots.sort(Comparator.comparing(AvailableSlot::getStart));

        List<AvailableSlot> merged = new ArrayList<>();
        AvailableSlot current = slots.get(0);

        for (int i = 1; i < slots.size(); i++) {
            AvailableSlot next = slots.get(i);

            if (!next.getStart().isAfter(current.getEnd())) {
                // 겹침 -> end 를 최대값으로 확장
                LocalDateTime newEnd =
                        next.getEnd().isAfter(current.getEnd()) ? next.getEnd() : current.getEnd();
                current = new AvailableSlot(current.getStart(), newEnd);
            } else {
                // 안 겹침 -> 현재 구간 확정
                merged.add(current);
                current = next;
            }
        }

        // 마지막 구간 추가
        merged.add(current);

        return merged;
    }

    // 전체 기간 - Busy = Free
    private List<AvailableSlot> invertBusySlots(List<AvailableSlot> busySlots,
                                                LocalDateTime periodStart,
                                                LocalDateTime periodEnd) {

        List<AvailableSlot> freeSlots = new ArrayList<>();

        if (busySlots.isEmpty()) {
            // 전체가 Free
            freeSlots.add(new AvailableSlot(periodStart, periodEnd));
            return freeSlots;
        }

        LocalDateTime cursor = periodStart;

        for (AvailableSlot busy : busySlots) {
            if (busy.getStart().isAfter(cursor)) {
                freeSlots.add(new AvailableSlot(cursor, busy.getStart()));
            }

            if (busy.getEnd().isAfter(cursor)) {
                cursor = busy.getEnd();
            }
        }

        if (cursor.isBefore(periodEnd)) {
            freeSlots.add(new AvailableSlot(cursor, periodEnd));
        }

        return freeSlots;
    }

    // 하루(00~24) 동안 Busy 가 전혀 없는 날짜
    private List<LocalDate> findFreeDays(List<AvailableSlot> busySlots,
                                         LocalDate startDate,
                                         LocalDate endDate) {

        List<LocalDate> freeDays = new ArrayList<>();

        for (LocalDate date = startDate;
             !date.isAfter(endDate);
             date = date.plusDays(1)) {

            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay(); // 24:00

            boolean hasBusy = busySlots.stream()
                    .anyMatch(slot ->
                            slot.getStart().isBefore(dayEnd) &&
                                    slot.getEnd().isAfter(dayStart)
                    );

            if (!hasBusy) {
                freeDays.add(date);
            }
        }

        return freeDays;
    }
}