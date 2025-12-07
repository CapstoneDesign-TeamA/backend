/**
 * File: GroupSummaryResponse.java
 * Description:
 *  - 그룹 목록 등에 사용되는 요약 정보 DTO
 *  - 그룹 ID, 이름, 멤버 수, 최근 활동 정보 포함
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupSummaryResponse {

    private Long groupId;   // 그룹 ID
    private String name;    // 그룹명
    private int memberCount; // 그룹 멤버 수
    private String lastActive; // 최근 활동 시각(문자열)
}