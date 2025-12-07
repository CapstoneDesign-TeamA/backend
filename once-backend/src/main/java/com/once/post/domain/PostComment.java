/**
 * File: PostComment.java
 * Description:
 *  - 게시글에 달리는 댓글 엔티티
 *  - 댓글 작성자, 소속 그룹, 대상 게시글 정보를 직접 보유하며
 *  - 생성 및 수정 시각을 자동으로 기록함
 */

package com.once.post.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_comment")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 댓글 ID

    @Column(nullable = false)
    private Long postId;    // 대상 게시글 ID

    @Column(nullable = false)
    private Long userId;    // 작성자 ID

    @Column(nullable = false)
    private Long groupId;   // 소속 그룹 ID

    @Column(nullable = false, length = 500)
    private String content; // 댓글 내용

    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime updatedAt; // 수정 시각

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;     // 생성 시각 기록
        this.updatedAt = now;     // 최초 수정 시각도 동일하게 기록
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // 수정 시각 갱신
    }
}