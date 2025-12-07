/**
 * File: AuthMapper.java
 * Description:
 *  - MyBatis 매퍼
 *  - 토큰 저장/조회/수정/삭제 처리
 *  - 이메일 기반 사용자 ID 조회
 */

package com.once.auth.mapper;

import com.once.auth.domain.Token;
import org.apache.ibatis.annotations.*;
import java.util.Optional;

@Mapper
public interface AuthMapper {

    // 토큰 저장
    @Insert("INSERT INTO tokens (user_id, refresh_token, access_token, expiry_date) " +
            "VALUES (#{userId}, #{refreshToken}, #{accessToken}, #{expiryDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertToken(Token token);

    // 토큰 갱신 (access_token + expiry_date)
    @Update("UPDATE tokens SET access_token = #{accessToken}, expiry_date = #{expiryDate} " +
            "WHERE refresh_token = #{refreshToken}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void updateToken(Token token);

    // 리프레시 토큰으로 조회
    @Select("SELECT * FROM tokens WHERE refresh_token = #{refreshToken}")
    Optional<Token> findByRefreshToken(String refreshToken);

    // 사용자 ID로 토큰 조회 (일부 컬럼만 조회)
    @Select("SELECT user_id, access_token, refresh_token FROM tokens WHERE user_id = #{userId}")
    Optional<Token> findTokenByUserId(Long userId);

    // 사용자 ID로 전체 토큰 조회
    @Select("SELECT * FROM tokens WHERE user_id = #{userId}")
    Optional<Token> findByUserId(Long userId);

    // 리프레시 토큰 삭제
    @Delete("DELETE FROM tokens WHERE refresh_token = #{refreshToken}")
    void deleteToken(String refreshToken);

    // 사용자 ID 기준 토큰 전체 삭제
    @Delete("DELETE FROM tokens WHERE user_id = #{userId}")
    void deleteTokensByUserId(Long userId);

    // 이메일로 user.id 조회
    @Select("SELECT id FROM users WHERE email = #{email}")
    Long findByEmail(String email);
}