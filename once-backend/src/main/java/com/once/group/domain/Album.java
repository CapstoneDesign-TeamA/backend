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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private Long meetingId;

    private String title;
    private String description;
    private String imageUrl;

    // AI 카테고리 제거됨
    // private String aiCategory;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}