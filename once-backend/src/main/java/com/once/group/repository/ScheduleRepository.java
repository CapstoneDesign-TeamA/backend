/**
 * File: ScheduleRepository.java
 * Description:
 *  - 그룹 단일 일정(Schedule) 엔티티 CRUD 리포지토리
 *  - groupId 기준 일정 목록 조회 제공
 */

package com.once.group.repository;

import com.once.group.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByGroupId(Long groupId); // 특정 그룹의 모든 일정 조회
}