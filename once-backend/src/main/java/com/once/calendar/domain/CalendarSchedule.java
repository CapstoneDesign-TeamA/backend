// src/main/java/com/once/calendar/domain/CalendarSchedule.java
package com.once.calendar.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_schedule")
@Getter
@NoArgsConstructor
public class CalendarSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    // 일정 생성자의 id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 일정이 속한 그룹 id
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "memo", length = 200)
    private String memo;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ScheduleType type; // PERSONAL / GROUP

    @Builder
    public CalendarSchedule(Long userId,
                            Long groupId,
                            String title,
                            String memo,
                            LocalDateTime startDateTime,
                            LocalDateTime endDateTime,
                            ScheduleType type) {
        this.userId = userId;
        this.groupId = groupId;
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
    }

    // 일정 수정을 위한 메서드
    public void update(String title, String memo,
                       LocalDateTime startDateTime,
                       LocalDateTime endDateTime) {
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}