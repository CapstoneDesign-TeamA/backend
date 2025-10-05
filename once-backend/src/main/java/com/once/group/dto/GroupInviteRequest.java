package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInviteRequest {
    private Long groupId;   // 초대할 그룹 ID
    private String email;   // 초대받는 사용자 이메일
}
