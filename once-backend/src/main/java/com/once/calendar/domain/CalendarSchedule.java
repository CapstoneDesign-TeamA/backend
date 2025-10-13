package com.once.calendar.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CalendarSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(nullable = false)
    private String title;
    private String memo;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private ScheduleType type; // PERSONAL, GROUP 등

    // 그룹 ID는 Group 엔티티와 연관관계를 맺는 것이 일반적입니다.
    // 예시: @ManyToOne private Group group;
    private Long groupId;

    // 생성자, 빌더 등 필요에 따라 추가
}

// 일정을 구분하기 위한 Enum 타입
enum ScheduleType {
    PERSONAL,
    GROUP
}