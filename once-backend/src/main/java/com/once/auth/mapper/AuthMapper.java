package com.once.auth.mapper;

import com.once.auth.domain.Token;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;
@Mapper
public interface AuthMapper {

    @Insert("INSERT INTO tokens (user_id, refresh_token,access_token,expiry_date) " +
            "VALUES (#{userId}, #{refreshToken},#{accessToken}, #{expiryDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertToken(Token token);

    @Update("update tokens set access_token = #{accessToken}, expiry_date = #{expiryDate} where refresh_token = #{refreshToken}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void updateToken(Token token);

    @Select("SELECT * FROM tokens WHERE refresh_token = #{refreshToken}")
    Optional<Token> findByRefreshToken(String refreshToken);


    @Select("SELECT user_id, access_token, refresh_token FROM tokens    WHERE user_id = #{userId}")
    Optional<Token> findTokenByUserId(Long userId);

    @Select("SELECT * FROM tokens WHERE user_id = #{userId}")
    Optional<Token> findByUserId(Long userId);

    @Delete("DELETE FROM tokens WHERE refresh_token = #{refreshToken}")
    void deleteToken(String refreshToken);

    @Delete("DELETE FROM tokens WHERE user_id = #{userId}")
    void deleteTokensByUserId(Long userId);

    @Select("SELECT id FROM users WHERE email = #{email}")
    Long findByEmail(String email);
}