/**
 * File: PostImage.java
 * Description:
 *  - 게시글과 연결된 이미지 정보를 저장하는 엔티티
 *  - 이미지 URL, 순서 정보, AI 분석 카테고리 등을 보유하며
 *  - 게시글(Post)과 1:N 관계로 매핑됨 (postId 직접 참조)
 */

package com.once.post.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_image")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;           // 이미지 ID

    @Column(nullable = false)
    private Long postId;       // 연결된 게시글 ID

    @Column(nullable = false)
    private String imageUrl;   // 이미지 URL

    private int orderIndex;    // 이미지 정렬 순서

    private String aiCategory; // AI 이미지 분석 결과 카테고리
}