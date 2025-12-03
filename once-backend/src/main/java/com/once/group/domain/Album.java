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
@Builder   // ← 반드시 추가!!
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private Long meetingId;  // 후기 앨범인 경우만 사용

    private String title;
    private String description;
    private String imageUrl;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}