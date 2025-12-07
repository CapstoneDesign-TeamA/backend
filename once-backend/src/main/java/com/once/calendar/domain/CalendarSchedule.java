/**
 * File: CalendarSchedule.java
 * Description:
 *  - 캘린더 일정 엔티티
 *  - 사용자/그룹/모임 일정 저장
 *  - 일정 수정 기능 포함
 */

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

    // 일정 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    // 일정 생성자(user) ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 일정이 속한 그룹 ID (없을 수도 있음)
    @Column(name = "group_id")
    private Long groupId;

    // 모임 일정인 경우 모임 ID
    @Column(name = "meeting_id")
    private Long meetingId;

    // 일정 제목
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 일정 메모
    @Column(name = "memo", length = 200)
    private String memo;

    // 일정 시작 시간
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    // 일정 종료 시간
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    // PERSONAL / GROUP 일정 종류
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ScheduleType type;

    @Builder
    public CalendarSchedule(Long userId,
                            Long groupId,
                            Long meetingId,
                            String title,
                            String memo,
                            LocalDateTime startDateTime,
                            LocalDateTime endDateTime,
                            ScheduleType type) {
        this.userId = userId;
        this.groupId = groupId;
        this.meetingId = meetingId;
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
    }

    // 일정 정보 수정
    public void update(String title,
                       String memo,
                       LocalDateTime startDateTime,
                       LocalDateTime endDateTime) {
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}