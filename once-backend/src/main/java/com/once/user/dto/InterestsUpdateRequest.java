/**
 * File: InterestsUpdateRequest.java
 * Description:
 *  - 사용자 관심사 수정 요청을 처리하는 DTO
 *  - 관심사는 최소 1개 이상 선택해야 하며, 문자열 리스트 형태로 전달됨
 */

package com.once.user.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class InterestsUpdateRequest {

    @NotEmpty(message = "관심사는 최소 1개 이상 선택해야 합니다.")
    private List<String> interests; // 수정할 관심사 목록

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}