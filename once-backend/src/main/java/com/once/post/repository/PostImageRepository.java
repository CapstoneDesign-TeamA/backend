package com.once.post.repository;

import com.once.post.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 게시글별 이미지 조회
    List<PostImage> findByPostIdOrderByOrderIndexAsc(Long postId);

    // 게시글 이미지 전체 삭제
    void deleteByPostId(Long postId);

    // 그룹 전체 이미지의 AI 카테고리를 가져오기 위한 새로운 쿼리
    @Query("""
        SELECT pi.aiCategory
        FROM PostImage pi
        JOIN Post p ON p.id = pi.postId
        WHERE p.groupId = :groupId
        AND pi.aiCategory IS NOT NULL
    """)
    List<String> findAiCategoriesByGroupId(Long groupId);
}