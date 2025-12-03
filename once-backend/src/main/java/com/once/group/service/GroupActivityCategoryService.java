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
        GroupActivityCategory rec = GroupActivityCategory.builder()
                .groupId(groupId)
                .userId(userId)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(rec);
    }

    public List<String> getAllCategories(Long groupId) {
        return repository.findAllByGroup(groupId);
    }

    public List<String> getRecentCategories(Long groupId) {
        return repository.findRecentCategories(groupId);
    }
}