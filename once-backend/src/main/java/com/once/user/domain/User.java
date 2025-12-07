/**
 * File: User.java
 * Description:
 *  - 서비스 사용자의 기본 정보를 보관하는 엔티티
 *  - 인증에 필요한 식별자와 프로필 관련 필드를 포함함
 */

package com.once.user.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false, unique = true)
    private String username; // 로그인 ID

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    private String nickname; // 닉네임

    @Column(length = 255)
    private String name; // 이름

    @Column(columnDefinition = "TEXT")
    private String profileImage; // 프로필 이미지 URL

    private String status; // 계정 상태

    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime updatedAt; // 수정 시각
}