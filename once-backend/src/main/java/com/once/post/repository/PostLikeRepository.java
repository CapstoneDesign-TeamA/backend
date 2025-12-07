/**
 * File: PostLikeRepository.java
 * Description:
 *  - 게시글 좋아요 정보 관리 Repository
 *  - 좋아요 여부 확인, 카운트 조회, 삭제 기능 제공
 */

package com.once.post.repository;

import com.once.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 사용자가 특정 게시글에 좋아요 눌렀는지 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 엔티티 단건 조회
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 게시글의 전체 좋아요 수
    int countByPostId(Long postId);

    // 게시글 삭제 시 좋아요 전체 제거
    void deleteByPostId(Long postId);
}