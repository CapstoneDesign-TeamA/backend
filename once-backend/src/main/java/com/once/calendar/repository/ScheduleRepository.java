package com.once.calendar.repository;

import com.once.calendar.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 기간 사이의 일정을 조회하는 쿼리 메서드 (예시)
    // JPQL을 사용하거나 QueryDSL로 더 복잡한 쿼리 작성 가능
    List<Schedule> findAllByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
}