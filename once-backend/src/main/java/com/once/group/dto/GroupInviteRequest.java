/**
 * File: GroupInviteRequest.java
 * Description:
 *  - 그룹 초대 요청 DTO
 *  - 이메일 기준으로 초대 진행
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInviteRequest {

    private Long groupId; // 초대할 그룹 ID
    private String email; // 초대받는 사용자 이메일
}