/**
 * File: PostImageRepository.java
 * Description:
 *  - 게시글 이미지 관련 JPA Repository
 *  - 이미지 조회, 삭제, AI 카테고리 조회 기능 제공
 */

package com.once.post.repository;

import com.once.post.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 게시글별 이미지 조회
    List<PostImage> findByPostId(Long postId);

    // 게시글별 이미지 조회 (orderIndex 기준 정렬)
    List<PostImage> findByPostIdOrderByOrderIndexAsc(Long postId);

    // 게시글의 이미지 전체 삭제
    void deleteByPostId(Long postId);

    // 특정 그룹에 속한 게시글들의 AI 이미지 카테고리 조회
    @Query("""
        SELECT pi.aiCategory
        FROM PostImage pi
        JOIN Post p ON p.id = pi.postId
        WHERE p.groupId = :groupId
        AND pi.aiCategory IS NOT NULL
    """)
    List<String> findAiCategoriesByGroupId(Long groupId);
}