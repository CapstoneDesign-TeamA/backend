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

    private Long groupId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long meetingId;

    private LocalDateTime createdAt;

    private String aiKeywords;
    private String aiSentiment;
    private String aiSummary;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}