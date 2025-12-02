package com.once.calendar.service;

import com.once.calendar.domain.CalendarSchedule;
import com.once.calendar.repository.CalendarScheduleRepository;
import com.once.group.repository.GroupMemberRepository;
import com.once.user.domain.User;
import com.once.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarGroupService {

    private final CalendarScheduleRepository scheduleRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    /**
     * 날짜별 바쁜 인원 수 계산
     */
    public Map<String, Integer> getBusyCountByDay(Long groupId, LocalDate start, LocalDate end) {

        List<Long> memberUserIds = groupMemberRepository.findUserIdsByGroupId(groupId);

        if (memberUserIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Long> groupIds = List.of(groupId);

        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                scheduleRepository.findAllSchedulesForGroupMembers(
                        memberUserIds, groupIds, startDt, endDt);

        Map<String, Integer> busyCountMap = new HashMap<>();

        for (CalendarSchedule s : schedules) {
            LocalDate sd = s.getStartDateTime().toLocalDate();
            LocalDate ed = s.getEndDateTime().toLocalDate();

            for (LocalDate d = sd; !d.isAfter(ed); d = d.plusDays(1)) {
                String key = d.toString();
                busyCountMap.put(key, busyCountMap.getOrDefault(key, 0) + 1);
            }
        }

        return busyCountMap;
    }


    /**
     * 그룹 멤버들의 월 전체 일정 조회
     * (개인 일정 + 그룹 일정 모두 포함)
     */
    public Map<String, Object> getMonthlySchedulesForGroupMembers(
            Long groupId, int year, int month) {

        List<Long> memberUserIds = groupMemberRepository.findUserIdsByGroupId(groupId);

        if (memberUserIds.isEmpty()) {
            return Map.of("schedules", List.of());
        }

        List<Long> groupIds = List.of(groupId);

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime startDt = ym.atDay(1).atStartOfDay();
        LocalDateTime endDt = ym.atEndOfMonth().atTime(23, 59, 59);

        List<CalendarSchedule> schedules =
                scheduleRepository.findAllSchedulesForGroupMembers(
                        memberUserIds, groupIds, startDt, endDt);

        List<Map<String, Object>> list = new ArrayList<>();

        for (CalendarSchedule s : schedules) {

            User user = userRepository.findById(s.getUserId()).orElse(null);
            String userName = (user != null)
                    ? (user.getNickname() != null ? user.getNickname() : user.getUsername())
                    : "알 수 없음";

            list.add(Map.of(
                    "title", s.getTitle(),
                    "startDate", s.getStartDateTime().toLocalDate().toString(),
                    "endDate", s.getEndDateTime().toLocalDate().toString(),
                    "type", s.getType().name(),
                    "userId", s.getUserId(),
                    "userName", userName
            ));
        }

        return Map.of("schedules", list);
    }
}