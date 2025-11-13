package com.once.calendar.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity(name = "CalendarSchedule")  // 임시
@Getter
@NoArgsConstructor
public class CalendarSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    // 일정 생성자의 id
    @Column(nullable = false)
    private Long userId;

    // 일정이 속한 그룹 id
    private Long groupId;

    @Column(nullable = false)
    private String title;

    @Column(length = 200)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType type; // PERSONAL, GROUP 등


    @Builder
    public CalendarSchedule(Long userId, Long groupId, String title, String memo, LocalDateTime startDateTime, LocalDateTime endDateTime, ScheduleType type) {
        this.userId = userId;
        this.groupId = groupId;
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
    }

    // 일정 수정을 위한 메서드
    public void update(String title, String memo, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}