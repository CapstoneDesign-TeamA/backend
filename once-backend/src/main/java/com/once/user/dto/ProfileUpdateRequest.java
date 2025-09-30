package com.once.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProfileUpdateRequest {
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    @Size(min = 1, max = 8, message = "닉네임은 한글 8자, 영문 14자까지 입력 가능합니다.")
    private String nickname;

    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다.")
    private List<String> interests;

    // Getters and Setters
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
}