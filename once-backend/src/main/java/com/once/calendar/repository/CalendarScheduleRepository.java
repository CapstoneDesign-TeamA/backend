package com.once.calendar.repository;

import com.once.calendar.domain.CalendarSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule, Long> {

    /*
    내가 생성한 개인 일정 (userId = userId and type = 'PERSONAL' 과
    내가 속한 그룹의 모든 일정 (groupId IN:groupIds)
    을 기간 (start ~ end) 내에서 모두 조회
     */

    @Query("SELECT s FROM CalendarSchedule s WHERE " +
            "((s.userId = :userId AND s.type = 'PERSONAL') OR (s.groupId IN :groupIds)) " +
            "AND (s.startDateTime < :end AND s.endDateTime > :start)")
    List<CalendarSchedule> findPersonalAndGroupSchedules(
            @Param("userId") Long userId,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /*
    그룹 멤버 일정 Free / Busy 분석
    특정 기간 내에 여러 명의 사용자가 속한 모든 일정(개인 + 그룹) 조회
     */
    @Query("SELECT s FROM CalendarSchedule s WHERE " +
           "(s.userId IN :memberUserIds OR s.groupId IN :groupIds) " +
            "AND (s.startDateTime < :end AND s.endDateTime > :start)")
    List<CalendarSchedule> findAllSchedulesForGroupMembers(
            @Param("memberUserIds") List<Long> memberUserIds,
            @Param("groupIds") List<Long> groupIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}