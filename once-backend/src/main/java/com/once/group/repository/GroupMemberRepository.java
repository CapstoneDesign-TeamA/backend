/**
 * File: GroupMemberRepository.java
 * Description:
 *  - 그룹 멤버 관련 조회/검증/삭제 기능 제공
 *  - 그룹-사용자 매핑 관리 및 멤버 ID 목록 조회 기능 포함
 */

package com.once.group.repository;

import com.once.group.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @Query("SELECT gm FROM GroupMember gm JOIN FETCH gm.group WHERE gm.userId = :userId")
    List<GroupMember> findByUserIdFetchGroup(Long userId); // 유저가 속한 그룹들 조회

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId); // 특정 멤버 조회

    List<GroupMember> findByGroupId(Long groupId); // 그룹 멤버 전체 조회

    List<GroupMember> findByUserId(Long userId); // 유저가 속한 그룹 목록 조회

    void deleteByGroupId(Long groupId); // 그룹 삭제 시 멤버도 삭제

    boolean existsByGroupIdAndUserId(Long groupId, Long userId); // 멤버 여부 체크

    long countByGroupId(Long groupId); // 멤버 수 카운트

    @Query("SELECT gm.userId FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<Long> findUserIdsByGroupId(Long groupId); // 그룹의 모든 멤버 userId 반환
}