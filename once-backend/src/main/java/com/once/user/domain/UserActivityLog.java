/**
 * UserActivityLog
 * 사용자 활동 로그 정보를 저장하는 도메인 클래스
 * - 로그인 / 회원가입 / 프로필 변경 등의 사용자 활동을 기록할 때 사용됨
 * - MyBatis 매핑 기반으로 DB에 저장되며 Entity가 아니라 단순 데이터 객체(DTO) 역할을 수행함
 */

package com.once.user.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserActivityLog {

    private Long id;
    private Long userId;          // 유저 ID
    private String activityType;  // 활동 종류
    private String description;   // 활동 설명
    private String ipAddress;     // 요청 IP
    private String userAgent;     // User-Agent
    private LocalDateTime createdAt; // 로그 생성 시간

    // 기본 생성자
    public UserActivityLog() {}

    // 필수 정보 생성자
    public UserActivityLog(Long userId, String activityType, String description) {
        this.userId = userId;
        this.activityType = activityType;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}