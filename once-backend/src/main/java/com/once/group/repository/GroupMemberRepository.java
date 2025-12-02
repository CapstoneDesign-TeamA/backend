package com.once.group.repository;

import com.once.group.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT gm FROM GroupMember gm JOIN FETCH gm.group WHERE gm.userId = :userId")
    List<GroupMember> findByUserIdFetchGroup(Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroupId(Long groupId);

    List<GroupMember> findByUserId(Long userId);

    void deleteByGroupId(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    long countByGroupId(Long groupId);
}
