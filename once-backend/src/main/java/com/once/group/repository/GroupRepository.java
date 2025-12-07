/**
 * File: GroupRepository.java
 * Description:
 *  - 그룹 엔티티 기본 CRUD 제공 JPA 리포지토리
 *  - 추가 커스텀 쿼리는 서비스에서 필요 시 확장 가능
 */

package com.once.group.repository;

import com.once.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}