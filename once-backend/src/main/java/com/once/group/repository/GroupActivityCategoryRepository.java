package com.once.group.repository;

import com.once.group.domain.GroupActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupActivityCategoryRepository extends JpaRepository<GroupActivityCategory, Long> {

    List<GroupActivityCategory> findByGroupId(Long groupId);

    @Query("SELECT g.category FROM GroupActivityCategory g WHERE g.groupId = :groupId ORDER BY g.createdAt DESC")
    List<String> findRecentCategories(Long groupId);

    @Query("""
        SELECT g.category
        FROM GroupActivityCategory g
        WHERE g.groupId = :groupId
        ORDER BY g.createdAt DESC
    """)
    List<String> findAllByGroup(Long groupId);
}