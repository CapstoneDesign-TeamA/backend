/**
 * File: Post.java
 * Description:
 *  - 그룹 피드에 등록되는 게시글 엔티티
 *  - 텍스트, 이미지, 모임 연동 게시글을 모두 표현하며
 *  - AI 분석 결과(키워드, 감정, 요약)도 함께 저장됨
 */

package com.once.post.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;     // 소속 그룹 ID
    private Long userId;      // 작성자 ID

    @Enumerated(EnumType.STRING)
    private PostType type;    // 게시글 유형(TEXT, IMAGE, MEETING 등)

    @Column(columnDefinition = "TEXT")
    private String content;   // 게시글 내용

    private Long meetingId;   // 모임 연동 게시글일 경우 모임 ID

    private LocalDateTime createdAt;

    // AI 분석 결과
    private String aiKeywords;
    private String aiSentiment;
    private String aiSummary;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now(); // 생성 시각 자동 지정
    }
}