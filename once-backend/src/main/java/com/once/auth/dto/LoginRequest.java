package com.once.auth.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    // Getters and Setters
    @NotBlank(message = "이메일은 필수 입력값입니다.") //为空或仅有空格时，抛出NotBlank异常
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

}