/**
 * File: MeetingRepository.java
 * Description:
 *  - Meeting 엔티티에 대한 기본 CRUD 및 조회 기능을 제공하는 Repository
 *  - 특정 그룹에 속한 모임 목록을 조회하는 메서드를 포함
 */

package com.once.meeting.repository;

import com.once.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // 그룹 ID 기반 모임 목록 조회
    List<Meeting> findByGroupId(Long groupId);
}