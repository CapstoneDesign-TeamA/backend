/**
 * File: Group.java
 * Description:
 *  - 그룹 엔티티
 *  - 그룹명, 설명, 대표 이미지, 생성일 등 기본 정보 저장
 *  - 그룹 소속 앨범 / 멤버 / 일정 / 투표 엔티티들과 연관 관계 구성
 *  - 그룹 삭제 시 모든 하위 데이터도 함께 삭제 (Cascade)
 */

package com.once.group.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_table")
@Getter
@Setter
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 그룹 ID (PK)

    @Column(nullable = false)
    private String name; // 그룹명

    private String description; // 그룹 설명

    private String imageUrl; // 그룹 대표 이미지 URL

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시각

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>(); // 그룹 앨범 목록

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>(); // 그룹 멤버 목록

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>(); // 그룹 일정 목록


    // 생성 시 createdAt 자동 기록
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}