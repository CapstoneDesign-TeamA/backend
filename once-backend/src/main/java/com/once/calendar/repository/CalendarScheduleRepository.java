// src/main/java/com/once/calendar/repository/CalendarScheduleRepository.java
package com.once.calendar.repository;

import com.once.calendar.domain.CalendarSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule, Long> {

    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "((s.userId = :userId AND s.type = 'PERSONAL') OR (s.groupId IN :groupIds)) " +
            "AND (s.startDateTime < :end AND s.endDateTime > :start)")
    List<CalendarSchedule> findPersonalAndGroupSchedules(
            @Param("userId") Long userId,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "(s.userId IN :memberUserIds OR s.groupId IN :groupIds) " +
            "AND (s.startDateTime < :end AND s.endDateTime > :start)")
    List<CalendarSchedule> findAllSchedulesForGroupMembers(
            @Param("memberUserIds") List<Long> memberUserIds,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}