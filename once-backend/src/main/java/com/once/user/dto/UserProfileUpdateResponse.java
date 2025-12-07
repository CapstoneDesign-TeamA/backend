/**
 * File: UserProfileUpdateResponse.java
 * Description:
 *  - 사용자 프로필 수정 후 반환되는 응답 DTO
 *  - 수정된 사용자 정보와 결과 메시지를 포함하여 프론트에서 즉시 반영 가능하도록 구성됨
 */

package com.once.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileUpdateResponse {

    private Long userId;        // 사용자 ID
    private String name;        // 수정된 이름
    private String email;       // 사용자 이메일
    private String profileImage; // 수정된 프로필 이미지 URL
    private String message;     // 처리 결과 메시지
}