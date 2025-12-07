/**
 * File: Album.java
 * Description:
 *  - 그룹 앨범 엔티티
 *  - 그룹 ID, 제목, 설명, 이미지 URL, 생성일 등 저장
 *  - 모임 종료 시 자동 앨범 생성에도 사용
 */

package com.once.group.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속 그룹
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // 모임 기반 자동 앨범 생성 시 연결되는 meetingId
    private Long meetingId;

    // 앨범 제목
    private String title;

    // 앨범 설명
    private String description;

    // 업로드된 대표 이미지 URL
    private String imageUrl;

    // 생성일
    private LocalDateTime createdAt;

    // 생성 시 자동으로 timestamp 기록
    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}