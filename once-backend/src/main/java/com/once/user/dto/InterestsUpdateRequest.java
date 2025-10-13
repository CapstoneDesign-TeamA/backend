package com.once.user.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class InterestsUpdateRequest {
    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다.")
    private List<String> interests;

    // Getters and Setters
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
}