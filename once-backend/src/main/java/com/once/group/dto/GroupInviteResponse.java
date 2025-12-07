/**
 * File: GroupInviteResponse.java
 * Description:
 *  - 그룹 초대 실행 후 반환되는 응답 DTO
 *  - 메시지 + 그룹 ID + 초대 대상 이메일 포함
 */

package com.once.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupInviteResponse {

    private String message; // 초대 완료 메시지
    private Long groupId;   // 그룹 ID
    private String email;   // 초대받은 사용자 이메일
}