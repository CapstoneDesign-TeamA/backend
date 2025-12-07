/**
 * File: GroupActivityCategoryService.java
 * Description:
 *  - 그룹 활동 카테고리를 저장하고 조회하는 서비스
 *  - AI 분석 결과를 기반으로 그룹의 최근 활동 성향을 기록
 *  - 그룹별 전체/최근 카테고리 조회 기능 제공
 */

package com.once.group.service;

import com.once.group.domain.GroupActivityCategory;
import com.once.group.repository.GroupActivityCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupActivityCategoryService {

    private final GroupActivityCategoryRepository repository;

    public void recordCategory(Long groupId, Long userId, String category) {
        // 그룹 활동 카테고리 기록 생성 후 저장
        GroupActivityCategory rec = GroupActivityCategory.builder()
                .groupId(groupId)
                .userId(userId)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(rec);
    }

    public List<String> getAllCategories(Long groupId) {
        // 그룹에 기록된 모든 활동 카테고리 조회
        return repository.findAllByGroup(groupId);
    }

    public List<String> getRecentCategories(Long groupId) {
        // 최신순으로 그룹 활동 카테고리 조회
        return repository.findRecentCategories(groupId);
    }
}