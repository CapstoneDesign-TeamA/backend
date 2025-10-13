package com.once.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupInviteResponse {
    private String message; // "초대가 완료되었습니다."
    private Long groupId;
    private String email;
}
