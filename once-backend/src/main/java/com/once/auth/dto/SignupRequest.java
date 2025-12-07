/**
 * File: SignupRequest.java
 * Description:
 *  - 회원가입 요청 DTO
 *  - 닉네임, 아이디, 비밀번호, 이메일 검증 포함
 *  - 관심사 목록 및 마케팅 수신 동의 여부 포함
 */

package com.once.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SignupRequest {

    // 닉네임 (한글/영문/숫자, 길이 제한)
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    @Size(min = 1, max = 8, message = "닉네임은 한글 8자, 영문 14자까지 입력 가능합니다.")
    private String nickname;

    // 아이디 (영문/숫자, 최소 4자)
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, message = "아이디는 4자 이상이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문자와 숫자로만 구성되어야 합니다.")
    private String username;

    // 비밀번호 (최소 4자)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;

    // 이메일
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    // 관심사 리스트
    private List<String> interests = new ArrayList<>();

    // 마케팅 수신 동의
    private Boolean marketingAgreed;
}