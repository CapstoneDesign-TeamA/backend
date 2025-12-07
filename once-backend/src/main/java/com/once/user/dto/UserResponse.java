/**
 * File: UserResponse.java
 * Description:
 *  - 사용자 정보를 단순 조회할 때 사용되는 응답 DTO
 *  - 최소한의 기본 정보만 담아 다른 서비스나 API 응답에서 재사용 가능하도록 설계됨
 */

package com.once.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long userId;        // 사용자 ID
    private String name;        // 사용자 이름
    private String email;       // 이메일
    private String profileImage; // 프로필 이미지 URL
}