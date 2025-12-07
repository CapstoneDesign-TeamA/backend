/**
 * File: LoginRequest.java
 * Description:
 *  - 로그인 요청 DTO
 *  - 이메일 · 비밀번호 검증 포함
 */

package com.once.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

    // 이메일 (NotBlank 검증)
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    // 비밀번호 (NotBlank 검증)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}