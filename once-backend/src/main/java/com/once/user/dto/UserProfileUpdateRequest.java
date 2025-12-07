/**
 * File: UserProfileUpdateRequest.java
 * Description:
 *  - 사용자의 기본 프로필 정보(name, profileImage)를 수정하기 위한 DTO
 *  - 간단한 정보만 업데이트할 때 사용되며, 별도 유효성 검증은 포함하지 않음
 */

package com.once.user.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    private String name;          // 변경할 사용자 이름
    private String profileImage;  // 새 프로필 이미지 URL
}