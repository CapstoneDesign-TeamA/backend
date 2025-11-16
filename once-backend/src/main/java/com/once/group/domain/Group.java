package com.once.group.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_table") // DB 테이블명
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

    private String imageUrl; // 그룹 대표 이미지

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시각

    // =========================
    // 연관관계 - 그룹 삭제 시 같이 삭제
    // =========================

    // 앨범들
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    // 멤버들
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>();

    // 일정들
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    // 투표들 (→ VoteOption까지 같이 삭제됨)
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    // 생성 시각 자동 세팅
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}