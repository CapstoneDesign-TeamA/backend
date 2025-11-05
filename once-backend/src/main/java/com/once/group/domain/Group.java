package com.once.group.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시각

}
