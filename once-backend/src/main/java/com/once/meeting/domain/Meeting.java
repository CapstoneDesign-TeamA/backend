/**
 * File: Meeting.java
 * Description:
 *  - 모임 엔티티 정의
 *  - 그룹 ID, 생성자 ID, 일정 정보, 생성 시간 필드 포함
 *  - 저장 시 createdAt 자동 설정
 */

package com.once.meeting.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 모임 ID

    @Column(name = "group_id", nullable = false)
    private Long groupId; // 그룹 ID

    @Column(name = "creator_id", nullable = false)
    private Long creatorId; // 모임 생성자 ID

    @Column(nullable = false)
    private String title; // 모임 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 모임 설명

    @Column(nullable = false)
    private LocalDate startDate; // 시작 날짜

    @Column(nullable = false)
    private LocalDate endDate; // 종료 날짜

    private String time; // 시간 정보
    private String location; // 모임 장소

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}