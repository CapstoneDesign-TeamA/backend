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
     * 본인의 개인 일정만 조회
     */
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "s.userId = :userId AND s.type = 'PERSONAL' " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start)")
    List<CalendarSchedule> findPersonalSchedulesOnly(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * 본인의 개인 일정 + 본인이 속한 그룹의 그룹 일정 조회
     * (개인 일정: s.userId = :userId AND type = PERSONAL)
     * (그룹 일정: s.groupId IN :groupIds AND type = GROUP)
     */
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "((s.userId = :userId AND s.type = 'PERSONAL') OR (s.groupId IN :groupIds AND s.type = 'GROUP')) " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start)")
    List<CalendarSchedule> findPersonalAndGroupSchedules(
            @Param("userId") Long userId,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * 여러 사용자들의 개인 일정만 조회 (그룹 캘린더용)
     * @param userIds 조회할 사용자 ID 목록
     * @param start 시작 일시
     * @param end 종료 일시
     * @return 해당 사용자들의 개인 일정 목록
     */
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "s.userId IN :userIds AND s.type = 'PERSONAL' " +
            "AND (s.startDateTime <= :end AND s.endDateTime >= :start) " +
            "ORDER BY s.startDateTime ASC")
    List<CalendarSchedule> findPersonalSchedulesByUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * ★ 그룹 멤버 전체의 개인 일정 + 그룹 일정 모두 조회 (기존 기능 - 필요시 사용)
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

    /**
     * 모임 ID로 연결된 캘린더 일정 삭제 (모임 삭제 시 사용)
     */
    void deleteByMeetingId(Long meetingId);
}