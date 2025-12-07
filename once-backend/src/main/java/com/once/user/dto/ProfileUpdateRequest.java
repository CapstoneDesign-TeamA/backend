/**
 * File: ProfileUpdateRequest.java
 * Description:
 *  - 사용자 프로필 수정 요청을 위한 DTO
 *  - 닉네임, 관심사, 이름, 프로필 이미지 정보를 포함하며
 *    유효성 검증(@Valid)을 통해 입력값을 체크함
 */

package com.once.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileUpdateRequest {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    @Size(min = 1, max = 8, message = "닉네임은 한글 8자, 영문 14자까지 입력 가능합니다.")
    private String nickname; // 수정할 닉네임

    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다.")
    private List<String> interests; // 관심사 목록

    private String name; // 사용자 이름 (선택값)
    private String profileImage; // 프로필 이미지 URL
}