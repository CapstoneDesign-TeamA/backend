/**
 * File: Schedule.java
 * Description:
 *  - 그룹 내부 일정(Schedule) 엔티티
 *  - date + time 기반 단일 일정 저장
 *  - group_id 연관 매핑
 *  - createdAt 자동 기록
 */

package com.once.group.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "schedule_table")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // 소속 그룹

    private String title; // 일정 제목
    private LocalDate date; // 일정 날짜
    private LocalTime time; // 일정 시간
    private String description; // 설명

    private LocalDateTime createdAt; // 생성 시각

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now(); // 생성 시 자동 기록
    }
}