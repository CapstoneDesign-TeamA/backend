package com.once.post.repository;

import com.once.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}