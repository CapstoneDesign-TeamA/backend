/**
 * File: InviteToken.java
 * Description:
 *  - 그룹 초대 링크에 사용되는 토큰 엔티티
 *  - groupId: 어느 그룹 초대인지 식별
 *  - token: 실제 초대 토큰 문자열 (unique)
 *  - expiredAt: 만료 시각
 *  - used: 초대 수락 여부
 *  - createdAt: 생성 시각 자동 기록
 */

package com.once.group.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invite_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false)
    private Long groupId; // 초대 대상 그룹 ID

    @Column(nullable = false, unique = true, length = 200)
    private String token; // 초대 토큰 문자열

    @Column(nullable = false)
    private LocalDateTime expiredAt; // 만료 시각

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false; // 사용 여부

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시각

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // 생성 시 자동 기록
        }
    }
}