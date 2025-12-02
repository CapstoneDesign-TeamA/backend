// src/main/java/com/once/calendar/repository/CalendarScheduleRepository.java
package com.once.calendar.repository;

import com.once.calendar.domain.CalendarSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule, Long> {

    /**
     * 기존: 내 개인 일정 + 내가 속한 그룹 일정 조회
     * (개인 일정: s.userId = :userId AND type = PERSONAL)
     * (그룹 일정: s.groupId IN :groupIds)
     */
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "((s.userId = :userId AND s.type = 'PERSONAL') OR (s.groupId IN :groupIds)) " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start)")
    List<CalendarSchedule> findPersonalAndGroupSchedules(
            @Param("userId") Long userId,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * ★ 핵심 기능
     * 그룹 멤버 전체의 개인 일정 + 그룹 일정 모두 조회
     *
     * memberUserIds  : 이 그룹(들)에 속한 모든 유저의 userId
     * groupIds       : 현재 로그인 유저가 속한 그룹 ID들 전체
     *
     * 조회되는 일정:
     * - 개인 일정  : (userId IN memberUserIds AND type = PERSONAL)
     * - 그룹 일정  : (groupId IN groupIds AND type = GROUP)
     */
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "((s.userId IN :memberUserIds AND s.type = 'PERSONAL') " +
            " OR (s.groupId IN :groupIds AND s.type = 'GROUP')) " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start)")
    List<CalendarSchedule> findAllSchedulesForGroupMembers(
            @Param("memberUserIds") List<Long> memberUserIds,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}