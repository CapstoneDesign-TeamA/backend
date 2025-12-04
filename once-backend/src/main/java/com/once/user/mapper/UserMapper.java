package com.once.user.mapper;

import com.once.user.domain.TermsAgreement;
import com.once.user.domain.User;
import com.once.user.domain.UserActivityLog;
import com.once.user.domain.UserInterest;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // User 관련 메서드
    @Insert("INSERT INTO users (username, password, email, nickname) " +
            "VALUES (#{username}, #{password}, #{email}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);

    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    Optional<User> findByNickname(String nickname);

    @Update("UPDATE users SET status = 'INACTIVE' WHERE id = #{userId}")
    void deactivateUser(Long userId);

    // UserInterest 관련 메서드
    @Insert("INSERT INTO user_interests (user_id, interest) VALUES (#{userId}, #{interest})")
    void insertUserInterest(@Param("userId") Long userId, @Param("interest") String interest);

    @Select("SELECT * FROM user_interests WHERE user_id = #{userId}")
    List<UserInterest> findInterestsByUserId(Long userId);


    // TermsAgreement 관련 메서드
    @Insert("INSERT INTO terms_agreements (user_id, term_type, agreed, agreed_version) " +
            "VALUES (#{userId}, #{termType}, #{agreed}, #{agreedVersion})")
    void insertTermsAgreement(TermsAgreement agreement);


    // 프로필 관련
    @Update("UPDATE users SET nickname = #{nickname}, name = #{name}, profile_image = #{profileImage}, updated_at = NOW() WHERE id = #{id}")
    void updateUserProfile(User user);

    @Delete("DELETE FROM user_interests WHERE user_id = #{userId}")
    void deleteUserInterests(Long userId);

    // 약관 관련
    @Select("SELECT * FROM terms_agreements WHERE user_id = #{userId}")
    List<TermsAgreement> findTermsAgreementsByUserId(Long userId);



    @Update("UPDATE terms_agreements SET agreed = #{agreed}, agreed_version = #{agreedVersion}, agreed_at = #{agreedAt} WHERE user_id = #{userId} AND term_type = #{termType}")
    void updateTermsAgreement(TermsAgreement agreement);


    // 활동 로그 관련

    @Select("SELECT * FROM user_activity_logs WHERE user_id = #{userId} ORDER BY created_at DESC ")
    List<UserActivityLog> findUserActivityLogs(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM user_activity_logs WHERE user_id = #{userId}")
    int countUserActivityLogs(Long userId);

    @Insert("INSERT INTO user_activity_logs (user_id, activity_type, description, ip_address, user_agent, created_at) " +
            "VALUES (#{user_id}, #{activity_type}, #{description}, #{ip_address}, #{user_agent}, #{created_at})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUserActivityLog(UserActivityLog log);
}