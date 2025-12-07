/**
 * File: InviteTokenRepository.java
 * Description:
 *  - 초대 토큰 엔티티용 JPA 리포지토리
 *  - 토큰 문자열 기반 단건 조회 기능 제공
 */

package com.once.group.repository;

import com.once.group.domain.InviteToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteTokenRepository extends JpaRepository<InviteToken, Long> {

    Optional<InviteToken> findByToken(String token); // 토큰 문자열로 조회
}