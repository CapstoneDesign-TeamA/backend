/**
 * File: PostRepository.java
 * Description:
 *  - 게시글 엔티티 조회 및 관리 Repository
 *  - 그룹별 최신순 게시글 조회 기능 제공
 */

package com.once.post.repository;

import com.once.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 그룹별 게시글 최신순 조회
    List<Post> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}