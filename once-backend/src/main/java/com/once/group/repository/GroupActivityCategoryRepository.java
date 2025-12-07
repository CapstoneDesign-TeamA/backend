/**
 * File: GroupActivityCategoryRepository.java
 * Description:
 *  - 그룹 활동 카테고리 기록 조회용 JPA 리포지토리
 *  - 최근 활동 카테고리 조회, 전체 카테고리 조회 등 제공
 */

package com.once.group.repository;

import com.once.group.domain.GroupActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupActivityCategoryRepository extends JpaRepository<GroupActivityCategory, Long> {

    List<GroupActivityCategory> findByGroupId(Long groupId); // 특정 그룹의 활동 카테고리 조회

    @Query("SELECT g.category FROM GroupActivityCategory g WHERE g.groupId = :groupId ORDER BY g.createdAt DESC")
    List<String> findRecentCategories(Long groupId); // 최신 카테고리 순으로 조회

    @Query("""
        SELECT g.category
        FROM GroupActivityCategory g
        WHERE g.groupId = :groupId
        ORDER BY g.createdAt DESC
    """)
    List<String> findAllByGroup(Long groupId); // 전체 기록 조회 (최근순)
}