package com.once.auth.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class SignupRequest {
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    @Size(min = 1, max = 8, message = "닉네임은 한글 8자, 영문 14자까지 입력 가능합니다.")
    private String nickname;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, message = "아이디는 4자 이상이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문자와 숫자로만 구성되어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다.")
    private List<String> interests;

    private Boolean marketingAgreed;

    // Getters and Setters
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public Boolean getMarketingAgreed() { return marketingAgreed; }
    public void setMarketingAgreed(Boolean marketingAgreed) { this.marketingAgreed = marketingAgreed; }
}
