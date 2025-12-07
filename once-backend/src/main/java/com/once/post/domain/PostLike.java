/**
 * File: PostLike.java
 * Description:
 *  - 게시글 좋아요 정보를 저장하는 엔티티
 *  - 하나의 게시글에 대해 한 사용자가 한 번만 좋아요를 누를 수 있도록
 *    (post_id, user_id) 조합에 유니크 제약이 적용됨
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
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        }
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       // PK

    @Column(name = "post_id", nullable = false)
    private Long postId;   // 게시글 ID

    @Column(name = "user_id", nullable = false)
    private Long userId;   // 사용자 ID
}