/**
 * File: GroupResponse.java
 * Description:
 *  - 그룹 기본 정보 응답 DTO
 *  - 그룹 생성/조회 시 클라이언트로 전달되는 데이터 구조
 */

package com.once.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {

    private Long groupId;            // 그룹 ID
    private String name;             // 그룹명
    private String description;      // 그룹 설명
    private String imageUrl;         // 대표 이미지 URL
    private LocalDateTime createdAt; // 생성 시각
}