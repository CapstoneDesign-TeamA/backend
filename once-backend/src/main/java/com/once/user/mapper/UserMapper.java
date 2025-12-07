/**
 * File: UserMapper.java
 * Description:
 *  - MyBatis 기반 사용자 관련 DB 접근을 담당하는 Mapper 인터페이스
 *  - 사용자(User), 관심사(UserInterest), 약관(TermsAgreement), 활동 로그(UserActivityLog)에 대한
 *    CRUD 및 조회 기능을 정의함
 *  - SQL은 MyBatis 어노테이션 방식으로 직접 매핑되어 동작함
 */

package com.once.user.mapper;

import com.once.user.domain.TermsAgreement;
import com.once.user.domain.User;
import com.once.user.domain.UserActivityLog;
import com.once.user.domain.UserInterest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // 사용자 생성
    @Insert("INSERT INTO users (username, password, email, nickname) " +
            "VALUES (#{username}, #{password}, #{email}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    // 사용자 조회 (id)
    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    // 사용자 조회 (email)
    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    // 사용자 조회 (username)
    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);

    // 사용자 조회 (nickname)
    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    Optional<User> findByNickname(String nickname);

    // 사용자 비활성화
    @Update("UPDATE users SET status = 'INACTIVE' WHERE id = #{userId}")
    void deactivateUser(Long userId);

    // 관심사 추가
    @Insert("INSERT INTO user_interests (user_id, interest) VALUES (#{userId}, #{interest})")
    void insertUserInterest(@Param("userId") Long userId, @Param("interest") String interest);

    // 관심사 조회
    @Select("SELECT * FROM user_interests WHERE user_id = #{userId}")
    List<UserInterest> findInterestsByUserId(Long userId);

    // 약관 동의 저장
    @Insert("INSERT INTO terms_agreements (user_id, term_type, agreed, agreed_version) " +
            "VALUES (#{userId}, #{termType}, #{agreed}, #{agreedVersion})")
    void insertTermsAgreement(TermsAgreement agreement);

    // 프로필 업데이트
    @Update("UPDATE users SET nickname = #{nickname}, name = #{name}, profile_image = #{profileImage}, updated_at = NOW() WHERE id = #{id}")
    void updateUserProfile(User user);

    // 관심사 전체 삭제
    @Delete("DELETE FROM user_interests WHERE user_id = #{userId}")
    void deleteUserInterests(Long userId);

    // 약관 목록 조회
    @Select("SELECT * FROM terms_agreements WHERE user_id = #{userId}")
    List<TermsAgreement> findTermsAgreementsByUserId(Long userId);

    // 약관 정보 업데이트
    @Update("UPDATE terms_agreements SET agreed = #{agreed}, agreed_version = #{agreedVersion}, agreed_at = #{agreedAt} " +
            "WHERE user_id = #{userId} AND term_type = #{termType}")
    void updateTermsAgreement(TermsAgreement agreement);

    // 활동 로그 조회
    @Select("SELECT * FROM user_activity_logs WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserActivityLog> findUserActivityLogs(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    // 활동 로그 개수 조회
    @Select("SELECT COUNT(*) FROM user_activity_logs WHERE user_id = #{userId}")
    int countUserActivityLogs(Long userId);

    // 활동 로그 추가
    @Insert("INSERT INTO user_activity_logs (user_id, activity_type, description, ip_address, user_agent, created_at) " +
            "VALUES (#{user_id}, #{activity_type}, #{description}, #{ip_address}, #{user_agent}, #{created_at})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUserActivityLog(UserActivityLog log);
}