/**
 * File: CalendarScheduleRepository.java
 * Description:
 *  - CalendarSchedule 엔티티 JPA Repository
 *  - 개인 일정 / 그룹 일정 조회 쿼리 제공
 *  - 그룹 멤버 전체 일정 조회 및 모임 삭제 시 일정 제거 기능 포함
 */

package com.once.calendar.repository;

import com.once.calendar.domain.CalendarSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule, Long> {

    // 개인 일정 조회
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "s.userId = :userId AND s.type = 'PERSONAL' " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start)")
    List<CalendarSchedule> findPersonalSchedulesOnly(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 개인 일정 + 속한 그룹 일정 조회
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "((s.userId = :userId AND s.type = 'PERSONAL') OR (s.groupId IN :groupIds AND s.type = 'GROUP')) " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start)")
    List<CalendarSchedule> findPersonalAndGroupSchedules(
            @Param("userId") Long userId,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 여러 사용자들의 개인 일정 조회 (그룹 캘린더)
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "s.userId IN :userIds AND s.type = 'PERSONAL' " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start) " +
            "ORDER BY s.startDateTime ASC")
    List<CalendarSchedule> findPersonalSchedulesByUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 그룹 멤버 전체 일정 조회 (개인 + 그룹)
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

    // 모임 삭제 시 연결된 일정 제거
    void deleteByMeetingId(Long meetingId);
}