/**
 * File: WebClientConfig.java
 * Description:
 *  - WebClient 설정
 *  - AI 서버 연동용 WebClient Bean 생성
 */

package com.once.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // AI 서버용 WebClient Bean
    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }
}