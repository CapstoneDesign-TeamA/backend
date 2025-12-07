/**
 * File: GroupInviteInfo.java
 * Description:
 *  - 초대 링크 조회 시 반환되는 그룹 정보 DTO
 *  - 그룹 기본 정보 + 멤버 수 포함
 */

package com.once.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteInfo {

    private Long groupId;       // 그룹 ID
    private String name;        // 그룹명
    private String description; // 그룹 설명
    private String imageUrl;    // 대표 이미지 URL
    private Integer membersCount; // 현재 멤버 수
}