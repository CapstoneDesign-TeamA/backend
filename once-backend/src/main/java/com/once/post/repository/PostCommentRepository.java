/**
 * File: PostCommentRepository.java
 * Description:
 *  - 게시글 댓글 관련 JPA Repository
 *  - 댓글 조회, 삭제, 개수 조회 기능 제공
 */

package com.once.post.repository;

import com.once.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 게시글별 댓글 목록 조회 (작성 시간 오름차순)
    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 게시글 삭제 시 댓글 전체 삭제
    void deleteByPostId(Long postId);

    // 특정 게시글의 댓글 수 조회
    int countByPostId(Long postId);
}