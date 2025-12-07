/**
 * File: GroupMember.java
 * Description:
 *  - 그룹 구성원 정보를 저장하는 엔티티
 *  - group_id + user_id 기반으로 멤버 포함 여부 확인
 *  - 그룹 내 역할(LEADER / MEMBER) 보유
 *  - joinedAt: 가입 시각 기록
 */

package com.once.group.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group; // 소속 그룹

    @Column(name = "user_id")
    private Long userId; // 사용자 ID

    @Enumerated(EnumType.STRING)
    private GroupRole role; // LEADER / MEMBER

    private LocalDateTime joinedAt; // 그룹 참여 일시
}