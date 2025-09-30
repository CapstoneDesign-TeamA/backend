package com.once.auth.config; // 确保包路径正确

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component // 关键：让Spring管理这个Bean
@ConfigurationProperties(prefix = "jwt") // 关键：绑定前缀为"jwt"的属性
public class JwtConfig {
    // 必须提供Getter和Setter方法

    private String secret;
    private long expiration;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    // 必须为 expiration 属性提供 Getter 和 Setter
    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) { // 这是解决错误的关键 setter 方法
        this.expiration = expiration;
    }
}