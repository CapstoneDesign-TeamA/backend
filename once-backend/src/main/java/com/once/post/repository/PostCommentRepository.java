package com.once.post.repository;

import com.once.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    void deleteByPostId(Long postId);

    // 댓글 개수 조회
    int countByPostId(Long postId);
}