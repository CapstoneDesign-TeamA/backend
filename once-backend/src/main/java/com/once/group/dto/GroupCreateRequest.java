/**
 * File: GroupCreateRequest.java
 * Description:
 *  - 그룹 생성 요청 DTO
 *  - name, description, imageUrl 포함
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCreateRequest {

    private String name;        // 그룹명
    private String description; // 그룹 설명
    private String imageUrl;    // 그룹 대표 이미지 URL
}