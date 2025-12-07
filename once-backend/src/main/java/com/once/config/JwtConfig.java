/**
 * File: JwtConfig.java
 * Description:
 *  - application.yml 의 jwt.* 설정 값을 읽어오는 설정 클래스
 *  - secret, expiration 자동 바인딩
 */

package com.once.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret;
    private long expiration;

    // JWT secret 값
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }

    // JWT 만료 시간 값
    public long getExpiration() {
        return expiration;
    }
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}