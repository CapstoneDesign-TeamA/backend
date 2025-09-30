package com.once.mapper;

import com.sharedcalendar.model.Token;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;
@Mapper
public interface AuthMapper {

    @Insert("INSERT INTO tokens (user_id, refresh_token, expiry_date) " +
            "VALUES (#{userId}, #{refreshToken}, #{expiryDate})")
    void insertToken(Token token);
    @Insert("INSERT INTO tokens (user_id, access_token,  expiry_date) " +
            "VALUES (#{userId}, #{accessToken}, #{expiryDate})")
    void insertToken1(Token token);

    @Select("SELECT * FROM tokens WHERE refresh_token = #{refreshToken}")
    Optional<Token> findByRefreshToken(String refreshToken);

    @Select("SELECT * FROM tokens WHERE user_id = #{userId}")
    Optional<Token> findByUserId(Long userId);

    @Delete("DELETE FROM tokens WHERE refresh_token = #{refreshToken}")
    void deleteToken(String refreshToken);

    @Delete("DELETE FROM tokens WHERE user_id = #{userId}")
    void deleteTokensByUserId(Long userId);
}