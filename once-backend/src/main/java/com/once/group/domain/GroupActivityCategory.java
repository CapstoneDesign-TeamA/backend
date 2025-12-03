package com.once.group.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_activity_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupActivityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    private Long userId;

    private String category;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}