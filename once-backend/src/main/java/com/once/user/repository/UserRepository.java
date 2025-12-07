/**
 * File: UserRepository.java
 * Description:
 *  - Spring Data JPA 기반 사용자(User) 엔티티에 대한 기본 CRUD 및 조회 기능을 제공하는 Repository 인터페이스
 *  - 이메일 및 사용자명(username)으로의 조회 기능을 추가적으로 지원하며
 *    인증·프로필 조회 과정에서 핵심적으로 사용됨
 */

package com.once.user.repository;

import com.once.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // username으로 사용자 조회
    Optional<User> findByUsername(String username);
}