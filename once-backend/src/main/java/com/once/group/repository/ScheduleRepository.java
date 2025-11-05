package com.once.group.repository;

import com.once.group.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByGroupId(Long groupId);
}