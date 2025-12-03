package com.once.post.repository;

import com.once.post.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostIdOrderByOrderIndexAsc(Long postId);

    void deleteByPostId(Long postId);
}