/**
 * File: GroupActivityCategory.java
 * Description:
 *  - 그룹 내 활동 카테고리 기록 엔티티
 *  - 사용자(userId)가 특정 그룹(groupId)에서 어떤 카테고리를 선택/활동했는지 저장
 *  - 추천 기능(AI 분석)에서 그룹의 최근 활동 성향 파악에 사용
 *  - createdAt 자동 기록
 */

package com.once.group.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_activity_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupActivityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    private Long groupId; // 활동이 속한 그룹 ID

    private Long userId; // 활동을 기록한 사용자 ID

    private String category; // 선택된 활동 카테고리

    private LocalDateTime createdAt; // 기록 시각

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // 생성 시각 자동 기록
        }
    }
}